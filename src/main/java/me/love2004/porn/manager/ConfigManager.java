package me.love2004.porn.manager;

import com.google.gson.*;
import me.love2004.porn.loveClient;
import me.love2004.porn.Feature;
import me.love2004.porn.modules.Module;
import me.love2004.porn.modules.player.NoDDoS;
import me.love2004.porn.modules.render.XRay;
import me.love2004.porn.setting.Bind;
import me.love2004.porn.setting.EnumConverter;
import me.love2004.porn.setting.Setting;
import me.love2004.porn.util.Util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager
        implements Util {
    public ArrayList<Feature> features = new ArrayList();
    public String config = "loveClient/config/";
    public boolean loadingConfig;
    public boolean savingConfig;

    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        switch (setting.getType()) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(Float.valueOf(element.getAsFloat()));
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            }
            case "Enum": {
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                } catch (Exception e) {
                }
                break;
            }
            default: {
                loveClient.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
            }
        }
    }

    private static void loadFile(JsonObject input, Feature feature) {
        for (Map.Entry entry : input.entrySet()) {
            String settingName = (String) entry.getKey();
            JsonElement element = (JsonElement) entry.getValue();
            if (feature instanceof FriendManager) {
                try {
                    loveClient.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                boolean settingFound = false;
                for (Setting setting : feature.getSettings()) {
                    if (!settingName.equals(setting.getName())) continue;
                    try {
                        ConfigManager.setValueFromJson(feature, setting, element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    settingFound = true;
                }
                if (settingFound) continue;
            }
            if (feature instanceof XRay) {
                feature.register(new Setting<Boolean>(settingName, Boolean.valueOf(true), v -> ((XRay) feature).showBlocks.getValue()));
                continue;
            }
            if (!(feature instanceof NoDDoS)) continue;
            NoDDoS noDDoS = (NoDDoS) feature;
            Setting setting = feature.register(new Setting<Boolean>(settingName, Boolean.valueOf(true), v -> noDDoS.showServer.getValue() != false && noDDoS.full.getValue() == false));
            noDDoS.registerServer(setting);
        }
    }

    public void loadConfig(String name) {
        this.loadingConfig = true;
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("loveClient").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        this.config = files.contains(new File("loveClient/" + name + "/")) ? "loveClient/" + name + "/" : "loveClient/config/";
        loveClient.friendManager.onLoad();
        for (Feature feature : this.features) {
            try {
                this.loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.saveCurrentConfig();
        this.loadingConfig = false;
    }

    public void saveConfig(String name) {
        this.savingConfig = true;
        this.config = "loveClient/" + name + "/";
        File path = new File(this.config);
        if (!path.exists()) {
            path.mkdir();
        }
        loveClient.friendManager.saveFriends();
        for (Feature feature : this.features) {
            try {
                this.saveSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.saveCurrentConfig();
        this.savingConfig = false;
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("loveClient/currentconfig.txt");
        try {
            if (currentConfig.exists()) {
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("loveClient", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("loveClient", ""));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadCurrentConfig() {
        File currentConfig = new File("loveClient/currentconfig.txt");
        String name = "config";
        try {
            if (currentConfig.exists()) {
                Scanner reader = new Scanner(currentConfig);
                while (reader.hasNextLine()) {
                    name = reader.nextLine();
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public void resetConfig(boolean saveConfig, String name) {
        for (Feature feature : this.features) {
            feature.reset();
        }
        if (saveConfig) {
            this.saveConfig(name);
        }
    }

    public void saveSettings(Feature feature) throws IOException {
        String featureName;
        Path outputFile;
        JsonObject object = new JsonObject();
        File directory = new File(this.config + this.getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!Files.exists(outputFile = Paths.get(featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json"))) {
            Files.createFile(outputFile);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this.writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(loveClient.moduleManager.modules);
        this.features.add(loveClient.friendManager);
        String name = this.loadCurrentConfig();
        this.loadConfig(name);
        loveClient.LOGGER.info("Config loaded.");
    }

    private void loadSettings(Feature feature) throws IOException {
        String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath)) {
            return;
        }
        this.loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            ConfigManager.loadFile(new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            loveClient.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
            ConfigManager.loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    public JsonObject writeSettings(Feature feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        for (Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
                continue;
            }
            if (setting.isStringSetting()) {
                String str = (String) setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }
            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public String getDirectory(Feature feature) {
        String directory = "";
        if (feature instanceof Module) {
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        }
        return directory;
    }
}

package org.wilson.world.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.wilson.world.dress.DressColor;
import org.wilson.world.dress.DressColorFamily;
import org.wilson.world.dress.HomeBase;
import org.wilson.world.dress.SeasonEnergy;
import org.wilson.world.quiz.QuizPair;
import org.wilson.world.util.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DressManager {
    private static final Logger logger = Logger.getLogger(DressManager.class);
    
    private Map<Integer, SeasonEnergy> energies = new HashMap<Integer, SeasonEnergy>();
    private Map<String, SeasonEnergy> namedEnergies = new HashMap<String, SeasonEnergy>();
    
    private Map<Integer, DressColorFamily> families = new HashMap<Integer, DressColorFamily>();
    private Map<String, DressColorFamily> namedFamilies = new HashMap<String, DressColorFamily>();
    
    private Map<Integer, DressColor> colors = new HashMap<Integer, DressColor>();
    private Map<String, DressColor> namedColors = new HashMap<String, DressColor>();
    
    private Map<Integer, HomeBase> homebases = new HashMap<Integer, HomeBase>();
    private Map<String, HomeBase> namedHomebases = new HashMap<String, HomeBase>();
    
    private List<List<QuizPair>> pairs = null;
    
    private static DressManager instance;
    
    private DressManager() {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("dress.json");
            String json = IOUtils.toString(in);
            JSONObject obj = JSONObject.fromObject(json);
            
            JSONArray energyArray = obj.getJSONArray("energies");
            for(int i = 0; i < energyArray.size(); i++) {
                JSONObject energyObj = energyArray.getJSONObject(i);
                SeasonEnergy energy = new SeasonEnergy();
                energy.id = i + 1;
                energy.name = energyObj.getString("name");
                JSONArray keywordArray = energyObj.getJSONArray("keywords");
                energy.keywords = new String [keywordArray.size()];
                for(int j = 0; j < energy.keywords.length; j++) {
                    energy.keywords[j] = keywordArray.getString(j);
                }
                energy.form = energyObj.getString("form");
                energy.line = energyObj.getString("line");
                energy.energy = energyObj.getString("energy");
                energy.color = energyObj.getString("color");
                energy.texture = energyObj.getString("texture");
                energy.contrast = energyObj.getString("contrast");
                energy.colors = energyObj.getString("colors");
                energy.size = energyObj.getString("size");
                
                this.energies.put(energy.id, energy);
                this.namedEnergies.put(energy.name, energy);
            }
            
            JSONArray familyArray = obj.getJSONArray("families");
            for(int i = 0; i < familyArray.size(); i++) {
                JSONObject familyObj = familyArray.getJSONObject(i);
                DressColorFamily family = new DressColorFamily();
                family.id = i + 1;
                family.name = familyObj.getString("name");
                family.spring = familyObj.getString("spring");
                family.summer = familyObj.getString("summer");
                family.autumn = familyObj.getString("autumn");
                family.winter = familyObj.getString("winter");
                family.clear = familyObj.getString("clear");
                family.soft = familyObj.getString("soft");
                if(familyObj.containsKey("icy_warm")) {
                    family.icyWarm = familyObj.getString("icy_warm");
                }
                if(familyObj.containsKey("icy_cool")) {
                    family.icyCool = familyObj.getString("icy_cool");
                }
                
                this.families.put(family.id, family);
                this.namedFamilies.put(family.name, family);
            }
            
            JSONArray colorArray = obj.getJSONArray("colors");
            for(int i = 0; i < colorArray.size(); i++) {
                JSONObject colorObj = colorArray.getJSONObject(i);
                DressColor color = new DressColor();
                color.id = i + 1;
                color.name = colorObj.getString("name");
                color.color = colorObj.getString("color");
                
                this.colors.put(color.id, color);
                this.namedColors.put(color.name, color);
            }
            
            JSONArray homebaseArray = obj.getJSONArray("homebases");
            for(int i = 0; i < homebaseArray.size(); i++) {
                JSONObject homebaseObj = homebaseArray.getJSONObject(i);
                HomeBase homebase = new HomeBase();
                homebase.id = i + 1;
                homebase.name = homebaseObj.getString("name");
                JSONArray itemsArray = homebaseObj.getJSONArray("items");
                homebase.items = new String [itemsArray.size()];
                for(int j = 0; j < homebase.items.length; j++) {
                    homebase.items[j] = itemsArray.getString(j);
                }
                
                this.homebases.put(homebase.id, homebase);
                this.namedHomebases.put(homebase.name, homebase);
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }
    
    public static DressManager getInstance() {
        if(instance == null) {
            instance = new DressManager();
        }
        
        return instance;
    }
    
    public List<SeasonEnergy> getSeasonEnergies() {
        return new ArrayList<SeasonEnergy>(this.energies.values());
    }
    
    public SeasonEnergy getSeasonEnergy(int id) {
        return this.energies.get(id);
    }
    
    public SeasonEnergy getSeasonEnergy(String name) {
        return this.namedEnergies.get(name);
    }
    
    public List<DressColorFamily> getDressColorFamilies() {
        return new ArrayList<DressColorFamily>(this.families.values());
    }
    
    public DressColorFamily getDressColorFamily(int id) {
        return this.families.get(id);
    }
    
    public DressColorFamily getDressColorFamily(String name) {
        return this.namedFamilies.get(name);
    }
    
    public List<DressColor> getDressColors() {
        return new ArrayList<DressColor>(this.colors.values());
    }
    
    public DressColor getDressColor(int id) {
        return this.colors.get(id);
    }
    
    public DressColor getDressColor(String name) {
        return this.namedColors.get(name);
    }
    
    public List<HomeBase> getHomeBases() {
        return new ArrayList<HomeBase>(this.homebases.values());
    }
    
    public HomeBase getHomeBase(int id) {
        return this.homebases.get(id);
    }
    
    public HomeBase getHomeBase(String name) {
        return this.namedHomebases.get(name);
    }
    
    public List<List<QuizPair>> getDressQuizPairs() {
        if(pairs == null) {
            this.initQuizPairs();
        }
        
        return pairs;
    }
    
    private void initQuizPairs() {
        this.pairs = new ArrayList<List<QuizPair>>();
        
        this.pairs.add(this.getEnergyKeywordsQuizPairs());
        this.pairs.add(this.getEnergyLineQuizPairs());
        this.pairs.add(this.getEnergyEnergyQuizPairs());
        this.pairs.add(this.getEnergyColorQuizPairs());
        this.pairs.add(this.getEnergyTextureQuizPairs());
        this.pairs.add(this.getEnergyContrastQuizPairs());
        this.pairs.add(this.getEnergyColorsQuizPairs());
        this.pairs.add(this.getEnergySizeQuizPairs());
        this.pairs.add(this.getFamilyNameQuizPairs());
        this.pairs.add(this.getFamilyTypeQuizPairs());
        this.pairs.add(this.getColorNameQuizPairs());
        this.pairs.add(this.getHomeBaserNameQuizPairs());
    }
    
    private int getNumOfQuizPairs() {
        int sum = 0;
        for(List<QuizPair> list : pairs) {
            sum += list.size();
        }
        return sum;
    }
    
    private List<QuizPair> getEnergyKeywordsQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Key words for [" + energy.name + "]";
            pair.bottom = energy.getKeywords();
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyLineQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Line for [" + energy.name + "]";
            pair.bottom = energy.line;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyEnergyQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Energy for [" + energy.name + "]";
            pair.bottom = energy.energy;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyColorQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Color for [" + energy.name + "]";
            pair.bottom = energy.color;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyTextureQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Texture for [" + energy.name + "]";
            pair.bottom = energy.texture;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyContrastQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Contrast for [" + energy.name + "]";
            pair.bottom = energy.contrast;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergyColorsQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Colors for [" + energy.name + "]";
            pair.bottom = energy.colors;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getEnergySizeQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(SeasonEnergy energy : this.energies.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Size for [" + energy.name + "]";
            pair.bottom = energy.size;
            pair.url = "javascript:jumpTo('season_energy_edit.jsp?id=" + energy.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private void addToDressColors(Map<String, DressColor> data, DressColorFamily family, String name) {
        String colorName = null;
        if("Spring".equals(name)) {
            colorName = family.spring;
        }
        else if("Summer".equals(name)) {
            colorName = family.summer;
        }
        else if("Autumn".equals(name)) {
            colorName = family.autumn;
        }
        else if("Winter".equals(name)) {
            colorName = family.winter;
        }
        else if("Clear".equals(name)) {
            colorName = family.clear;
        }
        else if("Soft".equals(name)) {
            colorName = family.soft;
        }
        else if("IcyWarm".equals(name)) {
            colorName = family.icyWarm;
        }
        else if("IcyCool".equals(name)) {
            colorName = family.icyCool;
        }
        
        if(colorName == null) {
            return;
        }
        
        DressColor color = this.getDressColor(colorName);
        if(color == null) {
            return;
        }
        
        data.put(name, color);
    }
    
    public Map<String, DressColor> getDressColors(DressColorFamily family) {
        if(family == null) {
            return Collections.emptyMap();
        }
        
        Map<String, DressColor> ret = new HashMap<String, DressColor>();
        
        this.addToDressColors(ret, family, "Spring");
        this.addToDressColors(ret, family, "Summer");
        this.addToDressColors(ret, family, "Autumn");
        this.addToDressColors(ret, family, "Winter");
        this.addToDressColors(ret, family, "Clear");
        this.addToDressColors(ret, family, "Soft");
        this.addToDressColors(ret, family, "IcyWarm");
        this.addToDressColors(ret, family, "IcyCool");
        
        return ret;
    }
    
    private List<QuizPair> getFamilyNameQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DressColorFamily family : this.families.values()) {
            Map<String, DressColor> data = this.getDressColors(family);
            for(DressColor color : data.values()) {
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "Family name of [" + color.name + "]<div style='background-color:" + color.color + ";width:100%;height:20px'/>";
                pair.bottom = family.name;
                pair.url = "javascript:jumpTo('dress_color_family_edit.jsp?id=" + family.id + "')";
                
                pairs.add(pair);
            }
        }
        
        return pairs;
    }
    
    private List<QuizPair> getFamilyTypeQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DressColorFamily family : this.families.values()) {
            Map<String, DressColor> data = this.getDressColors(family);
            for(Entry<String, DressColor> entry : data.entrySet()) {
                String type = entry.getKey();
                DressColor color = entry.getValue();
                QuizPair pair = new QuizPair();
                pair.id = id++;
                pair.top = "Family type of [" + color.name + "]<div style='background-color:" + color.color + ";width:100%;height:20px'/>";
                pair.bottom = type;
                pair.url = "javascript:jumpTo('dress_color_family_edit.jsp?id=" + family.id + "')";
                
                pairs.add(pair);
            }
        }
        
        return pairs;
    }
    
    private List<QuizPair> getColorNameQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(DressColor color : this.colors.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            pair.top = "Name of the color<div style='background-color:" + color.color + ";width:100%;height:20px'/>";
            pair.bottom = color.name;
            pair.url = "javascript:jumpTo('dress_color_edit.jsp?id=" + color.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    private List<QuizPair> getHomeBaserNameQuizPairs() {
        List<QuizPair> pairs = new ArrayList<QuizPair>();
        
        int id = this.getNumOfQuizPairs() + 1;
        for(HomeBase homebase : this.homebases.values()) {
            QuizPair pair = new QuizPair();
            pair.id = id++;
            StringBuilder sb = new StringBuilder();
            for(String item : homebase.items) {
                sb.append("<p>" + item + "</p>");
            }
            pair.top = sb.toString();
            pair.bottom = homebase.name;
            pair.url = "javascript:jumpTo('homebase_edit.jsp?id=" + homebase.id + "')";
            
            pairs.add(pair);
        }
        
        return pairs;
    }
}

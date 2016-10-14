package org.wilson.world.profile;

public class MBTIProfile {
    //E
    public int extraversion;
    
    //I
    public int introversion;
    
    //S
    public int sensing;
    
    //N
    public int intuition;
    
    //T
    public int thinking;
    
    //F
    public int feeling;
    
    //J
    public int judging;
    
    //P
    public int perceiving;
    
    public String getDisplay() {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        
        if(this.extraversion > this.introversion) {
            sb1.append("E");
            sb2.append("Extraversion,");
        }
        else {
            sb1.append("I");
            sb2.append("Introversion,");
        }
        
        if(this.sensing > this.intuition) {
            sb1.append("I");
            sb2.append("Sensing,");
        }
        else {
            sb1.append("S");
            sb2.append("Intuition,");
        }
        
        if(this.thinking > this.feeling) {
            sb1.append("T");
            sb2.append("Thinking,");
        }
        else {
            sb1.append("F");
            sb2.append("Feeling,");
        }
        
        if(this.judging > this.perceiving) {
            sb1.append("J");
            sb2.append("Judging");
        }
        else {
            sb1.append("P");
            sb2.append("Perceiving");
        }
        
        return sb1.toString() + " - " + sb2.toString();
    }
    
    public String getType() {
        StringBuilder sb = new StringBuilder();
        
        if(this.extraversion > this.introversion) {
            sb.append("E");
        }
        else {
            sb.append("I");
        }
        
        if(this.sensing > this.intuition) {
            sb.append("I");
        }
        else {
            sb.append("S");
        }
        
        if(this.thinking > this.feeling) {
            sb.append("T");
        }
        else {
            sb.append("F");
        }
        
        if(this.judging > this.perceiving) {
            sb.append("J");
        }
        else {
            sb.append("P");
        }
        
        return sb.toString();
    }
}

package readability;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.lang.Math;

public class Main {
    public static void main(String[] args) {
        //String filePath = "Readability Score (Java)\\task\\src\\readability\\in.txt";
        SentenceData information = new SentenceData();
        try(Scanner input = new Scanner(new File(args[0]))) {
            System.out.println("The text is:");
            while(input.hasNext()){
                String currentLine = input.nextLine();
                System.out.println(currentLine);
                information.updateData(currentLine);
            }
            System.out.println();
        }catch(FileNotFoundException e){
            System.out.println("Could not locate the file");
        }
        information.printData();
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String pickedOption = new Scanner(System.in).nextLine();
        information.printScores(pickedOption);
    }
}

class SentenceData{
    private double numberOfCharacters, numberOfSentences, numberOfWords, numberOfSylls, numberOfPolysylls;
    private double ariAge, fkAge, smogAge, clAge, averageAge;
    private double ariScore, smogScore, fleschKincaidScore, colemanLiauScore;
    public SentenceData(){
        this.numberOfCharacters = 0.0;
        this.numberOfSentences = 0.0;
        this.numberOfWords = 0.0;
        this.numberOfSylls = 0.0;
        this.numberOfPolysylls = 0.0;
    }
    public void updateData(String sentence){
        this.numberOfCharacters += sentence.replaceAll("\\s+", "").length();
        this.numberOfSentences += sentence.split("[\\.\\?!]+").length;
        String[] words = sentence.split("[\\.\\?!,]* ");
        this.numberOfWords += words.length;
        this.syllableCount(words);
        this.calculateARIScore();
        this.calculateCLScore();
        this.calculateFKScore();
        this.calculateSMOGScore();
        this.averageAge = (double)(this.ariAge + this.fkAge + this.smogAge + this.clAge) / 4;
    }

    private void calculateFKScore(){
        this.fleschKincaidScore = 0.39 * (this.numberOfWords/this.numberOfSentences) + 11.8 * (this.numberOfSylls / this.numberOfWords) - 15.59;
        this.fkAge = Math.round(this.fleschKincaidScore) + 6;
    }

    private void calculateSMOGScore(){
        this.smogScore = 1.043 * Math.sqrt(this.numberOfPolysylls * (30 / this.numberOfSentences)) + 3.1291;
        this.smogAge = Math.round(this.smogScore) + 6;
    }

    private void calculateCLScore(){
        double avChars = (this.numberOfCharacters * 100 / this.numberOfWords);
        double avSent = (this.numberOfSentences * 100 / this.numberOfWords);
        this.colemanLiauScore = 0.0588 * avChars - 0.296 * avSent - 15.8;
        this.clAge = Math.round(this.colemanLiauScore) + 6;
    }

    private void calculateARIScore(){
        this.ariScore = 4.71 * (this.numberOfCharacters / this.numberOfWords) + 0.5 * (this.numberOfWords / this.numberOfSentences) - 21.43;
        this.ariAge = Math.round(this.ariScore) + 6;
    }

    private void syllableCount(String[] words){
        for(String word : words){
            word = word.strip();
            int syll = word.split("[AEIOUYaeiouy]{1,2}+", -1).length - 1;
            if(word.charAt(word.length() - 1) == 'e' && syll > 1){
                syll -= 1;
            }
            if(syll > 2) {
                this.numberOfPolysylls += 1;
            }
            this.numberOfSylls += syll == 0 ? 1 : syll;
        }
    }

    public void printData(){
        System.out.printf("Words: %.0f\n", this.numberOfWords);
        System.out.printf("Sentences: %.0f\n", this.numberOfSentences);
        System.out.printf("Characters: %.0f\n", this.numberOfCharacters);
        System.out.printf("Syllables: %.0f\n", this.numberOfSylls);
        System.out.printf("Polysyllables: %d\n", (int)this.numberOfPolysylls);
    }

    public void printScores(String option){
        int choice = 0;
        if(option.equals("ARI")){
            choice = 1;
        }else if(option.equals("FK")){
            choice = 2;
        }else if(option.equals("SMOG")){
            choice = 3;
        }else if(option.equals("CL")){
            choice = 4;
        }else if(option.equals("all")){
            choice = 5;
        }
        switch(choice){
            case 1:
                System.out.printf("Automated Readability Index: %.2f (about %.0f-year-olds).\n", this.ariScore, this.ariAge);
                break;
            case 2:
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %.0f-year-olds).\n", this.fleschKincaidScore, this.fkAge);
                break;
            case 3:
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %.0f-year-olds).\n", this.smogScore, this.smogAge);
                break;
            case 4:
                System.out.printf("Coleman–Liau index: %.2f (about %.0f-year-olds).\n", this.colemanLiauScore, this.clAge);
                break;
            case 5:
                this.printScores("ARI");
                this.printScores("FK");
                this.printScores("SMOG");
                this.printScores("CL");
                System.out.println();
                System.out.printf("This text should be understood in average by %.2f-year-olds.", this.averageAge);
                break;
            default:
                System.out.println("Improper input!");
        }
    }
}
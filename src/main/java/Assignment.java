import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Assignment {

    public static void main(String[] args) {
        //Inputting the path
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the directory/file path:");
        String path = scanner.nextLine();
        scanner.close();

        int totalJavaFiles = 0;
        int totalUniqueJavaFiles = 0;

        int totalBlankLines = 0;
        int totalCommentLines = 0;
        int totalCodeLines = 0;
        Integer[] tempArray = new Integer[3];

        //one java file only
        if(path.endsWith(".java")){
            totalJavaFiles=1;
            totalUniqueJavaFiles = 1;
            tempArray = calculateResults(path);
            totalBlankLines = tempArray[0];
            totalCommentLines = tempArray[1];
            totalCodeLines = tempArray[2];
        }


        //Multiple Java files in the directory..
        else{
            try(Stream<Path> traverse = Files.walk(Paths.get(path))){
                List<String> listOfFiles = traverse.map(x->x.toString()).filter(f->f.endsWith(".java")).collect(Collectors.toList());
                totalJavaFiles = listOfFiles.size();
                listOfFiles = removeDuplicates(listOfFiles);
                totalUniqueJavaFiles = listOfFiles.size();
                for (String p: listOfFiles) {
                    tempArray = calculateResults(p);
                    totalBlankLines += tempArray[0];
                    totalCommentLines += tempArray[1];
                    totalCodeLines += tempArray[2];
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println(totalJavaFiles+"-"+totalUniqueJavaFiles+"-"+totalBlankLines+"-"+totalCommentLines+"-"+totalCodeLines);
    }

    private static List<String> removeDuplicates(List<String> results) {
        List<String> uniqueResults=new ArrayList<>();
        for(String path1:results){
            for(String path2:results){
                if(path1!=null && path2!=null && !path1.equalsIgnoreCase(path2) && isContentSame(path1,path2)){
                    results.set(results.indexOf(path2),null);
                }
            }
        }
        for(String s: results){
            if(s != null){
                uniqueResults.add(s);
            }
        }
        return uniqueResults;
    }

    private static boolean isContentSame(String path1, String path2) {
        File f1 = new File(path1);
        File f2 = new File(path2);
        if(f1.length() != f2.length()) {
            return false;
        }
        else {
            try(BufferedReader rdr1 = new BufferedReader(new FileReader(f1));
                BufferedReader rdr2 = new BufferedReader(new FileReader(f2))){
                int a = rdr1.read();
                int b = rdr2.read();
                while(a==b && a!=-1 && b!=-1){
                    a = rdr1.read();
                    b = rdr2.read();
                }
                if(a!=b) return false;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    private static Integer[] calculateResults(String path) {
        /*
            /Users/jot/IdeaProjects/HackerRank/src/com/company/Solution.java
            /Users/jot/IdeaProjects/HackerRank/src/com/company/
            /Users/jot/Downloads/Winter 2020/SM-SOEN6611/commons-io-master
        */
        int blankLineCount = 0;
        int commentLineCount = 0;
        int codeLineCount = 0;
        try(BufferedReader rdr = new BufferedReader(new FileReader(path))){
            String line = rdr.readLine();
            ArrayList<String> listOfLines = new ArrayList<>();
            boolean commentStartedflag = false;
            while(line!=null){
                listOfLines.add(line);
                if(line.length()==0)
                    blankLineCount++;
                String[] obj = line.split("\\s");
                line = "";
                for (String s:obj)
                    line+=s;
                if(line.startsWith("//"))
                    commentLineCount++;
                else if(line.startsWith("/*") || commentStartedflag){
                    commentStartedflag = true;
                    commentLineCount++;
                    if(line.endsWith("*/")) {
                        commentStartedflag = false;
                    }
                }
                line = rdr.readLine();
            }
            codeLineCount = listOfLines.size() - commentLineCount - blankLineCount;
        }
        catch (Exception e){
            System.out.println("HELLO"+e.getMessage());
        }
        return new Integer[]{blankLineCount,commentLineCount,codeLineCount};
    }
}
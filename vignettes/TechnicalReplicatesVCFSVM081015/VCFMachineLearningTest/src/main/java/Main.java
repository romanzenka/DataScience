import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by m102417 on 7/31/15.
 */
public class Main {


    public static void main(String[] args) {

//        if (args.length != 3) {
//            System.err.println(String.format("USAGE: java %s <vcf_file> <sample_name_1> <sample_name_2>", Main.class.getName()));
//            System.exit(1);
//        }

        final File vcf       = new File("/data/TechnicalReplicates250/MultiSample.vcf");
        final File diffFile  = new File("/tmp/foo.txt"); // /dev/stdout  "/tmp/foo.txt"

        try {

//            if (diffFile.exists()) {
//                diffFile.delete();
//                diffFile.createNewFile();
//            }

            processVCF(vcf, diffFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Program Finished!");

    }



    private static void processVCF(File vcf, File diffFile) throws IOException {

        // key = sample name
        // val = column index (1-based)
        Map<String,Integer> sampleColMap = new HashMap<String,Integer>();
        final int FORMAT_INDEX = 8;  // 0-based
        int sampleIndex1 = -1;       // 0-based
        int sampleIndex2 = -1;       // 0-based

        // counts
        int s1_GenotypePosCount = 0;
        int s2_GenotypePosCount = 0;
        int diff_count = 0;

        BufferedReader br = new BufferedReader(new FileReader(vcf));
        PrintWriter pw = new PrintWriter(new FileWriter(diffFile));
        try {
            String line;
            for (int i=0;(line=br.readLine()) != null;i++) {

                String[] cols = line.split("\t");

                //header line
                if ((line.length() > 0) && (line.charAt(0) == '#')) {
                    if (line.startsWith("##") == false) {
                        sampleColMap = processColumnHeader(cols);
                    }
                } else { //data line

                    int gtFieldIndex = getGTFieldIndex(cols[FORMAT_INDEX]);
                    int dpFieldIndex = getDPFieldIndex(cols[FORMAT_INDEX]);
                    String[] dataColumns = cols[FORMAT_INDEX].split(":");

                    int samplesHave = 0;
                    //check to see if all samples contain the variant, if so throw it out
                    for(String sample1 : sampleColMap.keySet()) {
                        sampleIndex1 = sampleColMap.get(sample1) - 1;
                        for (String sample2 : sampleColMap.keySet()) {
                            sampleIndex2 = sampleColMap.get(sample2) - 1;
                            int gt1 = isGenotypePositive(gtFieldIndex, cols[sampleIndex1]);
                            int gt2 = isGenotypePositive(gtFieldIndex, cols[sampleIndex2]);
                            if(gt1 == 1 && gt2 ==1){
                                samplesHave++;
                            }
                        }
                    }

                    //if all samples do not contain the variant, then there could be differences that we need to count...
                    if(samplesHave/sampleColMap.keySet().size() != sampleColMap.keySet().size()) {
                        //go through all sample pairs
                        for (String sample1 : sampleColMap.keySet()) {
                            sampleIndex1 = sampleColMap.get(sample1) - 1;
                            for (String sample2 : sampleColMap.keySet()) {
                                if (!sample1.equals(sample2)) { //They are not the same sample...
                                    sampleIndex2 = sampleColMap.get(sample2) - 1;
                                    StringBuilder sb = new StringBuilder();
                                    //append the class label...
                                    sb.append(isSame(sample1, sample2));
                                    sb.append(" ");
                                    sb.append(stringify(cols[sampleIndex1], gtFieldIndex, dpFieldIndex, dataColumns, 1));
                                    sb.append(" ");
                                    sb.append(stringify(cols[sampleIndex2], gtFieldIndex, dpFieldIndex, dataColumns, 3));
                                    //append the location so we can make sense of this junk!
                                    sb.append(" 5:");
                                    sb.append(cols[1]);
                                    //if(i%5==0) {  //used this bit of code to extract a subset for training / testing...
                                        pw.write(sb.toString());
                                        pw.write("\n");
                                    //}

                                }
                            }
                        }
                    }

                }
            }
        } finally {
            br.close();
            pw.close();
        }

    }

    private static String stringify(String sample, int gtIndex, int dpIndex, String[] dataColumns, int index){
        //System.out.println(sample);
        int gt = isGenotypePositive(gtIndex, sample);
        String[] tokens = sample.split(":");
        //if the sample is null or malformed, init it to zeros!
        if(tokens.length != dataColumns.length){
            //System.out.println("problem?");
            tokens = new String[dataColumns.length];
            for(int i=0;i<dataColumns.length; i++){
                tokens[i]="0";
            }
        }
        StringBuilder out = new StringBuilder();
        out.append(index);
        out.append(":");
        out.append(gt);
        out.append(" ");
        int i = 0;
        for(String token : tokens){
            //if(i != gtIndex){
            if(i == dpIndex){
                index++;
                out.append(index);
                out.append(":");
                out.append(token.replaceAll("%",""));
                if(i<tokens.length-1){
                    out.append(" ");
                }
            }
            i++;
        }
        return out.toString();
    }

    /**
     * returns if the samples are from the same technical replicate --this is quick and dirty, not something that will work in general.
     * @param sample1
     * @param sample2
     * @return
     */
    private static String isSame(String sample1, String sample2){
        //System.out.println(sample1 + ":" + sample2);
        String s1 = sample1.substring(0,6);
        String s2 = sample2.substring(0,6);
        if(s1.equalsIgnoreCase(s2)){
            return "+1"; //equal
        }
        return "-1"; //not equal
    }


    private static Map<String,Integer> processColumnHeader(String[] cols) {

        // key = sample name
        // val = column index (1-based)
        Map<String,Integer> sampleColMap = new HashMap<String,Integer>();

        for (int i=9; i < cols.length; i++) {
            String name = cols[i];
            int oneBasedIndex = i+1;
            sampleColMap.put(name, oneBasedIndex);
        }

        return sampleColMap;
    }


    private static int getGTFieldIndex(String formatCol) {

        String[] fields = formatCol.split(":");
        for (int i=0; i < fields.length; i++) {
            if (fields[i].equalsIgnoreCase("GT")) {
                return i;
            }
        }
        return -1;
    }

    private static int getDPFieldIndex(String formatCol) {

        String[] fields = formatCol.split(":");
        for (int i=0; i < fields.length; i++) {
            if (fields[i].equalsIgnoreCase("DP")) {
                return i;
            }
        }
        return -1;
    }


    private static int isGenotypePositive(int gtFieldIndex, String sampleCol) {
        if (sampleCol.equals(".")) {
            return 0;
        }
        String[] fields = sampleCol.split(":");
        String gtField = fields[gtFieldIndex];
        String[] vals = gtField.split("(\\|)|(/)");

        for (String val: vals) {
            if (!val.equals(".") && !val.equals("0")) {
                return 1;
            }
        }
        return 0;
    }
}

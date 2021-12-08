package uj.java.w7.insurance;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.Map.Entry.comparingByValue;

public class FloridaInsurance {

    public static void main(String[] args) {
        String zipDir = "C:\\Users\\mroza\\IdeaProjects\\07-florida-insurance\\FL_insurance.csv.zip";
        String zipFile = "FL_insurance.csv";
        var records  = readZipFile(zipDir,zipFile);
        records.remove(0);
        List<InsuranceEntry> insuranceEntryList = convertEntry(records);

        long cCountries = countCountries(insuranceEntryList);
        double dSumOf2012 = sumOf2012(insuranceEntryList);
        var lIncreaseByCountry = largestIncreaseByCountry(insuranceEntryList,10);

        List<Pair<String, Double>> pairList = new ArrayList<>();
        for (var key : lIncreaseByCountry.keySet()){
            Pair<String, Double> pair = new Pair<>(key, lIncreaseByCountry.get(key));
            pairList.add(pair);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##", symbols);

        try{
            File fileCountries = new File("count.txt");
            File fileSumOf2012 = new File("tiv2012.txt");
            File fileMostValuable = new File("most_valuable.txt");

            FileWriter fileWriter = new FileWriter("count.txt");
            fileWriter.write(String.valueOf(cCountries));
            fileWriter.close();

            fileWriter = new FileWriter("tiv2012.txt");
            fileWriter.write(df.format(dSumOf2012));
            fileWriter.close();


            fileWriter = new FileWriter("most_valuable.txt");
            fileWriter.write("country,value\n");

            for (int i=0; i<10; i++){
                fileWriter.write(pairList.get(i).getName() + "," + df.format(pairList.get(i).getValue()));
                fileWriter.write("\n");
            }
            fileWriter.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Map<String, Double> largestIncreaseByCountry(List<InsuranceEntry> entries, int limit) {

        return entries.stream()
                .collect(Collectors.toMap(
                        InsuranceEntry::getCounty,
                        InsuranceEntry::getDiff,
                        Double::sum)).entrySet()
                .stream()
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue)-> oldValue, LinkedHashMap::new));
    }

    public static double sumOf2012(List<InsuranceEntry> entries){
        return entries.stream()
                .map(InsuranceEntry::getTiv_2012)
                .reduce(Double::sum)
                .get();
    }

    public static long countCountries(List<InsuranceEntry> entries){
        return entries.stream()
                .map(InsuranceEntry::getCounty)
                .distinct()
                .count();
    }

    public static List<List<String>> readZipFile(String zipDir, String zipFileName) {
        List<List<String>> records = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zipDir);
            ZipEntry zipEntry = zipFile.getEntry(zipFileName);
            InputStream inputStream = zipFile.getInputStream(zipEntry);

            InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String row;
            while ( (row = br.readLine()) != null){
                String[] values = row.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }

    public static List<InsuranceEntry> convertEntry(List<List<String>> records){
        List<InsuranceEntry> insuranceEntryList = new ArrayList<>();
        for(var list : records){
            insuranceEntryList.add(new InsuranceEntry(list));
        }
        return insuranceEntryList;
    }


}

class Pair<T,V>{
    T name;
    V value;

    public Pair(T name, V value) {
        this.name = name;
        this.value = value;
    }

    public T getName() {
        return name;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}

class InsuranceEntry{
    public int policyID;
    public String statecode;
    public String county;
    public String eq_site_limit;
    public String hu_site_limit;
    public String fl_site_limit;
    public String fr_site_limit;
    public double tiv_2011;
    public double tiv_2012;
    public String eq_site_deductible;
    public String hu_site_deductible;
    public String fl_site_deductible;
    public String fr_site_deductible;
    public String point_latitude;
    public String point_longitude;
    public String line;
    public String construction;
    public String point_granularity;

    public InsuranceEntry(List<String> entry) {
        this.policyID = Integer.parseInt(entry.get(0));
        this.statecode = entry.get(1);
        this.county = entry.get(2);
        this.eq_site_limit = entry.get(3);
        this.hu_site_limit = entry.get(4);
        this.fl_site_limit = entry.get(5);
        this.fr_site_limit = entry.get(6);
        this.tiv_2011 = Double.parseDouble(entry.get(7));
        this.tiv_2012 = Double.parseDouble(entry.get(8));
        this.eq_site_deductible = entry.get(9);
        this.hu_site_deductible = entry.get(10);
        this.fl_site_deductible = entry.get(11);
        this.fr_site_deductible = entry.get(12);
        this.point_latitude = entry.get(13);
        this.point_longitude = entry.get(14);
        this.line = entry.get(15);
        this.construction = entry.get(16);
        this.point_granularity = entry.get(17);
    }

    @Override
    public String toString() {
        return " " + policyID +
                " " + statecode  +
                " " + county +
                " " + tiv_2011 +
                " " + tiv_2012;
    }

    public String getCounty() {
        return county;
    }

    public double getTiv_2012() {
        return tiv_2012;
    }

    public double getDiff() {
        return tiv_2012-tiv_2011;
    }
}

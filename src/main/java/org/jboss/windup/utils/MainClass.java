/**
 * 
 */
package org.jboss.windup.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.windup.utils.model.ExportReportModelToCSV;
import org.jboss.windup.utils.model.ReportModel;

/**
 * @author mnovotny
 *
 */
public class MainClass
{
    public static final Logger logger = LogManager.getLogger(MainClass.class);
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        CsvCompareOptions csvCompareOptions = new CsvCompareOptions();
        try
        {
            csvCompareOptions.parse(args);
        }
        catch(Exception exp ) {
            System.exit(2);
        }
        
        try
        {
            CsvWindupExportLoader loader1 = new CsvWindupExportLoader(new URL(csvCompareOptions.getOldFile()), csvCompareOptions.getOldDelimiter());
            CsvWindupExportLoader loader2 = new CsvWindupExportLoader(new URL(csvCompareOptions.getNewFile()), csvCompareOptions.getNewDelimiter());
            WindupReportComparison reportCmp = new WindupReportComparison(loader1.parseCSV(), loader2.parseCSV());
            List<ReportModel> listDiff = null;
            if ( csvCompareOptions.isExportedBothDifferences() ) {
                listDiff =reportCmp.compareNewAndOldReportsWithDiffLines();
            } else {
                listDiff = reportCmp.compareNewAndOldReports();    
            }
            
            if (listDiff != null && listDiff.size()> 0) {
                logger.debug(listDiff);
                (new ExportReportModelToCSV(listDiff)).export(new File("diff.csv"));
                System.exit(1);
            } else {
                System.exit(0);
            }
        }
        catch (MalformedURLException e) {
           logger.error("Wrong CSV file path " + e.getLocalizedMessage());
        } catch (IOException ioe) {
           logger.error("Error while exporting resulted difference to file - " + ioe.getLocalizedMessage());
        }
    }

}

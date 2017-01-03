
package Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {
    
    File file;
    FileWriter writer;
    private int logIndex;

    public Log(String filename) throws IOException{
        
        this.file = new File(filename);
        this.file.setReadable(true);
        this.writer = new FileWriter(this.file, true); 
        
        if(!this.file.exists()){
            this.file.createNewFile();
            this.logIndex = 1;
        }
        else{
            FileReader reader = new FileReader(this.file);
            BufferedReader input = new BufferedReader(reader);
         
            String last = null, line;
                
            while ((line = input.readLine()) != null) 
                last = line;
            
            if(last!=null){
                String parts[] = last.split("@");
                this.logIndex = Integer.parseInt(parts[0]) + 1;
            }
            else
                this.logIndex = 1;           
            
        }   
    }
    
    public void writeLog(int term, String command) throws IOException{
        
        String entry = Integer.toString(this.logIndex) + "@" + Integer.toString(term) + "@" + command + "\n";

        // Writes the content to the file
        this.writer.write(entry); 
        this.writer.flush();
      
        this.logIndex ++;
    }
    
    public int[] getLogLastEntry() throws IOException{
        
        FileReader reader = new FileReader(this.file);
        BufferedReader input = new BufferedReader(reader);
        
        String last = null, line;
              
        int[] info = new int[2];
        info[0]=1;
        info[1]=1;
        
        while ((line = input.readLine()) != null) 
            last = line;

        if(last!=null){
            String parts[] = last.split("@");
            info[1]= Integer.parseInt(parts[1]); 
            info[0]= Integer.parseInt(parts[0]);
        }

        return info;      
    }  

    public int lookForTerm(int index) throws IOException{
        //retorna -1 se index não existe no log
        //retorna term se index existe no log
        
        FileReader reader = new FileReader(this.file);
        BufferedReader input = new BufferedReader(reader);        
        
        String last = null, line;
        
        while ((line = input.readLine()) != null){ 
            last = line;
            String parts[] = last.split("@");
            int Logindex = Integer.parseInt(parts[0]);
            if (Logindex == index){
                return Integer.parseInt(parts[1]);
            }       
        }
        
        return -1;
    }

    public int checkForConflicts(int[] newEntryTerms, int  prevLogLeaderIndex) throws FileNotFoundException, IOException{
        //retorna 0 se não houver conflito
        // retorna o primeiro index do Log onde houve conflito

        FileReader reader = new FileReader(this.file);
        BufferedReader input = new BufferedReader(reader);        
        
        String line;
        int i = -1;
        
        while ((line = input.readLine()) != null){ 
            String parts[] = line.split("@");
            int LogIndex = Integer.parseInt(parts[0]);
            int LogTerm = Integer.parseInt(parts[1]);
            
            //percorrer o log até se chegar ao index = prevLogIndex do leader
            if (LogIndex < prevLogLeaderIndex)
                continue;
            
            //a partir do momento que se encontra no log o prevLogIndex do leader,
            //comparar termLog com newEntryTerm, se forem diferentes -> conflito
            else{
                if(newEntryTerms[i] != LogTerm)
                    return LogIndex;
                i++;
                prevLogLeaderIndex++;       
            }        
        }
        
        return 0;       
        
    }
    
    public void deleteEntries(int mismatchIndex) throws FileNotFoundException, IOException{
        //remove entries of the log from index to EndOfFile
        
        String filename = "tmp.txt";
        File auxfile = new File(filename);
        
        //copy logcontents for auxfile
        copyContents(this.file, auxfile);
        
        //empty log file
        PrintWriter emptyWriter = new PrintWriter(this.file);
        emptyWriter.print("");
        emptyWriter.close();
        
        //copy contents from auxfile to this.file until mismatchIndex
        FileReader reader = new FileReader(auxfile);
        BufferedReader input = new BufferedReader(reader);        
        String line;
        while ((line = input.readLine()) != null){ 
            String parts[] = line.split("@");
            int LogIndex = Integer.parseInt(parts[0]);
            if (LogIndex < mismatchIndex)
                this.writer.write(line); 
            else if(LogIndex == mismatchIndex)
                return;
        }
        
        //delete auxfile
        boolean delete = auxfile.delete();
        if(!delete)
            System.out.println("[Log] unable to delete file");
        
    }
    
    public void copyContents(File infile, File outfile) throws FileNotFoundException, IOException{
        //code from http://beginnersbook.com/2014/05/how-to-copy-a-file-to-another-file-in-java/
    	
        FileInputStream instream = null;
	FileOutputStream outstream = null;
 
        instream = new FileInputStream(infile);
        outstream = new FileOutputStream(outfile);

        byte[] buffer = new byte[6144];

        int length;
        while ((length = instream.read(buffer)) > 0)
            outstream.write(buffer, 0, length);

        instream.close();
        outstream.close();
     
    }

}
   
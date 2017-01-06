
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
    
    public int writeLog(int[] term, String[] command) throws IOException{
        
        System.out.println("[DEBUG] ANTES WRITE current term: " + this.logIndex);
        
        int i, size = term.length;
        
        for (i=0; i<size; i++){
            String entry = Integer.toString(this.logIndex) + "@" + Integer.toString(term[i]) + "@" + command[i] + "\n";
            System.out.println("[DEBUG] entry: " + entry);
            // Writes the content to the file
            this.writer.write(entry); 
            this.writer.flush();
            
            this.logIndex ++;
            
        }
       
        
        return (this.logIndex-1);
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
        int i = 0;
        
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
                if( i < newEntryTerms.length ){
                    if(newEntryTerms[i] != LogTerm)
                        return LogIndex;
                    i++;
                }
                prevLogLeaderIndex++;       
            }        
        }
        
        return 0;       
        
    }
    
    public void deleteEntries(int mismatchIndex) throws FileNotFoundException, IOException{
        //remove entries of the log from index to EndOfFile
        
        String filename = "tmp.txt";
        File auxfile = new File(filename);
        auxfile.createNewFile();
        
        //copy logcontents for auxfile
        copyContents(this.file, auxfile);
        
        //empty log file
        PrintWriter emptyWriter = new PrintWriter(this.file);
        emptyWriter.print("");
        emptyWriter.close();
        this.logIndex = 1;
        
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
    
    public String getLogContents(int initIndex) throws FileNotFoundException, IOException{
        //retorna o log a partir do index dado
        
        String log = "";
        System.out.println("[DEBUG] logContents. initIndex: " + initIndex);
        
        FileReader reader = new FileReader(this.file);
        BufferedReader input = new BufferedReader(reader);        
        String line;
        while ((line = input.readLine()) != null){ 
           
            String parts[] = line.split("@");
            
            if (Integer.parseInt(parts[0]) < initIndex)
                continue;
            else
            //String: term1:command1:term2:command2:term3:command3:
            log = log + ":" + parts[1] + ":" + parts[2];
        }
        
        System.out.println("[DEBUG] getLogContents" + log);
        return log;
        
    }
    
    public void updateLog(String message) throws FileNotFoundException, IOException{
        
        //message format: UPDATE_LOG:term1:command1:term2:command2:term3:command3:
        
        //get new entries
        String newEntries[] = message.split(":");   
        
        int nNewEntries = (newEntries.length-1)/2; //newEntries.length-1 tem de dar sempre um n.o par
        int[] newEntryTerms = new int[nNewEntries];
        String[] newEntryCommands = new String[nNewEntries];

        int i, j=0; //começa em 1 para ignorar o newEntris[0]=AppendEntry
        for(i=1; i<newEntries.length-1; i+=2){
            newEntryTerms[j] = Integer.parseInt(newEntries[i]);
            newEntryCommands[j] = newEntries[i+1];
            j++;
        }        
        
        //updateLod with new entries
        writeLog(newEntryTerms,newEntryCommands);


    }
}
   
/**
 *IMPLEMENTATION OF APPROX-TSP ALGORITHM [TSP.java]
 *
 *
 *
 **/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.math.*;

public class TSP {
    String graphDescrip;
    String tour;
    String previousErr;
    int numCities;
    adjacencyList adjList, adjLstMst;
    FiboHeap prioriQ;
    aLstVertex root;

    public TSP() {
        graphDescrip = new String("");
        tour = new String("");
        previousErr = new String();
        numCities = 0;
        adjList = new adjacencyList();
        adjLstMst = new adjacencyList();
        prioriQ = new FiboHeap();
        root = null;
    }

    public boolean setInput(String s) {
        graphDescrip = graphDescrip.concat(s);
        return parseInput(); //RESULT FROM parseInput METHOD
    }

    public boolean parseInput() {
        StringReader reader;
        StreamTokenizer parser;
        boolean gotNumCities = false;
        boolean expectX = false;
        boolean expectY = false;
        double inNum, xVal, yVal;
        xVal = yVal = 0;

        try {
            reader = new StringReader(graphDescrip);
            parser = new StreamTokenizer(reader);
            parser.eolIsSignificant(true);

            int cityCount = 0;
            while(parser.nextToken() != StreamTokenizer.TT_EOF) {
                if(parser.ttype == StreamTokenizer.TT_NUMBER) {
                    return false;
                } 
                else if(parser.ttype == StreamTokenizer.TT_NUMBER) {
                    inNum = parser.nval;
                    if (inNum < 0) { return false; }

                    if (!gotNumCities) {
                        gotNumCities = true;
                        numCities = (int) inNum;
                        expectX = true;
                    }
                    else if(expectX) {
                        xVal = inNum;
                        expectX = false;
                        expectY = true;
                    }
                    else if(expectY) {
                        yVal = inNum;
                        expectX = true;
                        expectY = false;
                        adjList.addVertex(xVal, yVal);
                        cityCount++;
                    }
                }
            }

            if(!gotNumCities) {
                return false;
            }
            else if(expectY) {
                return false;
            }
            else if(cityCount == 0) {
                previousErr = previousErr.concat("NO CITIES IN INPUT FILE");
                return false;
            }
            else if(cityCount != numCities) {
                previousErr = previousErr.
                    concat("NUMBER OF CITIES SPECIFIED DOES NOT MATCH INPUT");
                return false;
            }
        }
        catch (IOException e) {
            previousErr = previousErr.concat(e.getMessage());
            return false;
        }

        return true;
    }


    public String getTour() {
        return tour;
    }


    public void runTSP() {
        ArrayList extractedLst = new ArrayList();
        
        boolean source = true;
        aLstVertex entry = new aLstVertex();
        for (Iterator i = adjList.vertexArray.Iterator(); i.hasNext();) {
            entry = (aLstVertex) i.next();
            if (!source) {
                entry.setKey(9999999);
            } 
            else {
                entry.setKey(0);
                root = new aLstVertex(entry);
                System.out.println("MST ROOT:" + root.getName());
                source = false;
            }
            prioriQ.insert(entry);
        }
//             System.out.println("PRIORITY QUEUE LOADED:");
//             System.out.println(" MIN: "+ prioriQ.getMin().getName());
//             System.out.println(" ROOTLIST:" + prioriQ.rootList() + "\n");

        aLstEdge edge = nenw aLstEdge();
        aLstVertex w = new aLstVertex();
        
//         while (prioriQ.getSize() > 0) {
//             System.out.println("QSIZE:" + prioriQ.getSize());
//             aLstVertex v = (aLstVertex) prioriQ.extractMin();
//         }

        boolean inQ;
        while(prioriQ.getSize() > 0) {
            System.out.println("QSIZE:" + prioriQ.getSize());
            aLstVertex v = (aLstVertex) prioriQ.extractMin();
            extractedLst.add(v);

            System.out.println("u: " + v.getName());
            for (Iterator i = v.edgeList.iterator(); i.hasNext();) {
                inQ = true;
                edge = (aLstEdge) i.next();

                for (Iterator j = extractedLst.iterator(); j.hasNext();) {
                    w = (aLstVertex) j.next();
                    if (edge.sink == w) { inQ = false;}
                }

                if (inQ && (edge.weight < edge.sink.getKey())) {
                    edge.sink.piSet(v);
                    System.out.println("v: " + edge.sink.getName() + " w:"+edge.weight);
                    prioriQ.decreaseKey(edge.sink, edge.weight);
                }
            }
        }


//         //Create adjacency list for MST result
//         aLstVertex v;
//         aLstVertex pi;
        
//         for(Iterator i = adjList.vertexArray.iterator(); i.hasNext();) {
//             v = (aLstVertex) i.next();
//             v.flushEdges();
//         }

//         for(Iterator i = adjList.vertexArray.iterator(); i.hasNext();) {
//             v = (aLstVertex) i.next();
//             if ((pi = v.piGet()) != null) {
//                 pi.addOutEdge(v,1.0); //EDGE WEIGHT IS IRRELEVANT HERE
//             }
//         }





    }


    public String dumpMST() {
        String S = new String("");
        // S = S.concat(root.xGet() + "," + root.yGet());
        return S;
    }

    public static void main(String[] args) {
        System.out.println("KNUTH-MORRIS-PRATT");
        System.out.println("ASSUMED INPUT FILE: inTSP");
        System.out.println("OUTPUT FILE: outTSP");

        File f;
        FileReader in = null;
        boolean noText = false;
        TSP tourGuide = new TSP();

        try {
            
            //OPEN THE DEFAULT INPUT FILE

            f = new File(System.getProperty("user.dir"),"inTSP");
            in = new FileReader(f);
            String tmp = new String("");
            char[] buffer = new char[4096];
            int len;
            while((len = in.read(buffer)) != -1) {
                String s = new String(buffer, 0, len);
                tmp = tmp.concat(s);
            }


            if (!tourGuide.setInput(tmp)) {
                System.out.println("ABORT! INPUT FILE HAS AN ERROR");
            } else {
                System.out.println(tourGuide.adjList.showVerticies());
                tourGuide.adjList.makeComplete();
                //RUN APPROX TSP ALGORITHM
                tourGuide.runTSP();
                //PRODUCE DEFAULT OUTPUT FILE "outTSP"
                f = new File(System.getProperty("user.dir"), "outTSP");
                FileWriter out = new FileWriter(f);
                tmp = new String();
                tmp = tourGuide.getTour();
                out.write(tmp,0,tmp.length());
                out.close();
            }
        }
        catch (IOException e) {
            System.out.println(e.getClass().getName()+": " + e.getMessage());
        }
        finally { try {in.close(); } catch (IOException e) {} }
    }
    
}



package com.charliemouse.cambozola.shared;

import java.io.*;
import java.util.Hashtable;
import java.net.URLConnection;

/**
 ** com/charliemouse/cambozola/shared/CamStream.java </br> Copyright (C) Andy Wilcock, 2001. </br> Available from
 * http://www.charliemouse.com </br> </br>
 * 
 * This file is part of the Cambozola package (c) Andy Wilcock, 2001. </br> Available from http://www.charliemouse.com
 * </br> </br>
 * 
 * Cambozola is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version. </br> </br>
 * 
 * Cambozola is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * </br> </br>
 * 
 * You should have received a copy of the GNU General Public License along with Cambozola; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </br>
 * 
 * </br> </br>
 *
 *
 * @see CamStream CamStream for more information.
 *
 *
 **/
public class StreamSplit {
    public static final String BOUNDARY_MARKER_PREFIX  = "--";
    public static final String BOUNDARY_MARKER_TERM    = BOUNDARY_MARKER_PREFIX;

	protected DataInputStream m_dis;
	private boolean m_streamEnd;


	public StreamSplit(DataInputStream dis)
	{
		m_dis = dis;
		m_streamEnd = false;
	}


	public Hashtable readHeaders() throws IOException
	{
		Hashtable ht = new Hashtable();
		String response;
		boolean satisfied = false;

		do {
            //noinspection deprecation
            response = m_dis.readLine();
			if (response == null) {
				m_streamEnd = true;
				break;
			} else if (response.equals("")) {
                if (satisfied) {
				    break;
                } else {
                    // Carry on...
                }
			} else {
                satisfied = true;
            }
            addPropValue(response, ht);
        } while (true);
		return ht;
	}

    protected static void addPropValue(String response, Hashtable ht)
    {
        int idx = response.indexOf(":");
        if (idx == -1) {
            return;
        }
        String tag = response.substring(0, idx);
        String val = response.substring(idx + 1).trim();
        ht.put(tag.toLowerCase(), val);
    }


    public static Hashtable readHeaders(URLConnection conn)
    {
        Hashtable ht = new Hashtable();
        int i = 0;
        do {
            String key = conn.getHeaderFieldKey(i);
            if (key == null) {
                if (i == 0) {
                    i++;
                    continue;
                } else {
                    break;
                }
            }
            String val = conn.getHeaderField(i);
            ht.put(key.toLowerCase(), val);
            i++;
        } while (true);
        return ht;
    }


	public void skipToBoundary(String boundary) throws IOException
	{
		getStreamToReadToBoundary(boundary);
	}


	public InputStream getStreamToReadToBoundary(String boundary) throws IOException
	{
		return piper.go(boundary);
	}

	
	private final BoundaryStreamPiper piper = new BoundaryStreamPiper();
	
	private class BoundaryStreamPiper implements Runnable {
		
		public BoundaryStreamPiper() {
			new Thread(this).start();
		}
		
		public PipedInputStream go(String boundary) throws IOException {
			this.boundary = boundary;
			
			in = new PipedInputStream();
			out = new PipedOutputStream(in);
			
			return in;
		}

		public PipedInputStream in;
		public PipedOutputStream out;
		String boundary;
		
		public synchronized void run() {
			while(true) {
				
				while(out==null) {
					try {
						Thread.sleep(1);
					} catch(Exception ex) {}
				}
				
				StringBuffer lastLine = new StringBuffer();
				int lineidx = 0;
				int chidx = 0;
				byte ch;
				do {
					try {
						ch = m_dis.readByte();
					} catch (IOException e) {
						m_streamEnd = true;
						break;
					}
					if (ch == '\n' || ch == '\r') {
						//
						// End of line... Note, this will now look for the boundary
		                // within the line - more flexible as it can handle
		                // arfle--boundary\n  as well as
		                // arfle\n--boundary\n
						//
						String lls = lastLine.toString();
		                int idx = lls.indexOf(BOUNDARY_MARKER_PREFIX);
		                if (idx != -1) {
		                    lls = lastLine.substring(idx);
		                    if (lls.startsWith(boundary)) {
		                        //
		                        // Boundary found - check for termination
		                        //
		                        String btest = lls.substring(boundary.length());
		                        if (btest.equals(BOUNDARY_MARKER_TERM)) {
		                            m_streamEnd = true;
		                        }
		                        chidx = lineidx+idx;
		                        break;
		                    }
						}
						lastLine = new StringBuffer();
						lineidx = chidx + 1;
					} else {
						lastLine.append((char) ch);
					}
					chidx++;
					try {
						out.write(ch);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} while (true);

				try {
					out.flush();
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				out = null;
			}
			
		}
	}

	public boolean isAtStreamEnd()
	{
		return m_streamEnd;
	}
}

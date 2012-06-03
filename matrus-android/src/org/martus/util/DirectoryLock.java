/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

To the extent this copyrighted software code is used in the 
Miradi project, it is subject to a royalty-free license to 
members of the Conservation Measures Partnership when 
used with the Miradi software as specified in the agreement 
between Benetech and WCS dated 5/1/05.
*/

package org.martus.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

public class DirectoryLock
{
	public static class AlreadyLockedException extends Exception {}

	public void lock(File directory) throws IOException, AlreadyLockedException
	{
		lockFile = new File(directory, "lock");
		FileOutputStream tempLockStream = new FileOutputStream(lockFile);
		FileLock lock = tempLockStream.getChannel().tryLock();
		if(lock == null)
			throw new AlreadyLockedException();
		lockStream = tempLockStream;
	}
	
	public void close() throws IOException
	{
		if(lockStream == null)
			return;
		
		FileOutputStream tempLockStream = lockStream;
		lockStream = null;
		tempLockStream.close();
		lockFile.delete();
	}
	
	public boolean isLocked()
	{
		return (lockStream != null);
	}
	
	public boolean getDirectoryLock(File directory) throws IOException
	{
		try
		{
			lock(directory);
			return true;
		}
		catch(AlreadyLockedException e)
		{
			return false;
		}
	}
	
	private File lockFile;
	private FileOutputStream lockStream;
}

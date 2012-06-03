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

*/

package org.martus.common.database;

import java.io.File;

import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.packet.UniversalId;

public class ClientFileDatabase extends FileDatabase
{
	public ClientFileDatabase(File directory, MartusCrypto security)
	{
		super(directory, security);
	}

	public boolean mustEncryptLocalData()
	{
		return true;
	}
	
	public void verifyAccountMap() throws MartusUtilities.FileVerificationException, MissingAccountMapSignatureException
	{
		if( !accountMapSignatureFile.exists() )
		{
			throw new MissingAccountMapSignatureException();
		}
		MartusUtilities.verifyFileAndSignature(accountMapFile, accountMapSignatureFile, security, security.getPublicKeyString());
	}
	
	public boolean doesAccountMapExist()
	{
		return accountMapFile.exists();
	}

	public boolean doesAccountMapSignatureExist()
	{
		return accountMapSignatureFile.exists();
	}
	
	protected DatabaseKey getDatabaseKey(File accountDir, String bucketName, UniversalId uid)
	{
		return DatabaseKey.createSealedKey(uid);
	}
	
	protected String getBucketPrefix(DatabaseKey key)
	{
		return defaultBucketPrefix;
	}
}

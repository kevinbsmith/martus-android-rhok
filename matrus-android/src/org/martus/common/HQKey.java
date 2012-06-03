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
package org.martus.common;

import org.martus.common.crypto.MartusCrypto;
import org.martus.util.StreamableBase64.InvalidBase64Exception;


public class HQKey
{
	public HQKey(String publicKey)
	{
		this(publicKey, "");
	}

	public HQKey(String publicKey, String label)
	{
		super();
		this.publicKey = publicKey;
		setLabel(label);
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String newLabel)
	{
		if(newLabel == null)
			newLabel = "";
		label = newLabel;
	}
	
	public String getPublicKey()
	{
		return publicKey;
	}
	
	public String getRawPublicCode() throws InvalidBase64Exception
	{
		return MartusCrypto.computePublicCode(publicKey);
	}
	
	public String getPublicCode() throws InvalidBase64Exception
	{
		return MartusCrypto.computeFormattedPublicCode(publicKey);
	}
	
	public int hashCode()
	{
		return publicKey.hashCode();
	}
	
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof HQKey))
			return false;
		
		HQKey other = (HQKey)rawOther;
		return publicKey.equals(other.publicKey) && label.equals(other.label);
	}
	
	private String publicKey;
	private String label;
}

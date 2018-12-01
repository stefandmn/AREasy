package org.areasy.runtime.plugins.sso.ntlm.engine;

/*
 * Copyright (c) 2007-2018 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrObject;

public class GroupMembership extends NdrObject
{
	private int attributes;
	private int relativeId;

	public GroupMembership()
	{
		//nothing to do here
	}

	public GroupMembership(int relativeId, int attributes)
	{
		this.relativeId = relativeId;
		this.attributes = attributes;
	}

	public void decode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.align(4);

		relativeId = ndrBuffer.dec_ndr_long();
		attributes = ndrBuffer.dec_ndr_long();
	}

	public void encode(NdrBuffer ndrBuffer)
	{
		ndrBuffer.align(4);

		ndrBuffer.enc_ndr_long(relativeId);
		ndrBuffer.enc_ndr_long(attributes);
	}

	public int getAttributes()
	{
		return attributes;
	}

	public int getRelativeId()
	{
		return relativeId;
	}
}

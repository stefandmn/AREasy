package org.areasy.common.velocity.context;

/*
 * Copyright (c) 2007-2015 AREasy Runtime
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

/**
 * interface to bring all necessary internal and user contexts together.
 * this is what the AST expects to deal with.  If anything new comes
 * along, add it here.
 * <p/>
 *
 * @version $Id: InternalContextAdapter.java,v 1.1 2008/05/25 22:33:10 swd\stefan.damian Exp $
 */

public interface InternalContextAdapter extends InternalHousekeepingContext, Context, InternalWrapperContext, InternalEventContext
{
}

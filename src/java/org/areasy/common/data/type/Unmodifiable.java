package org.areasy.common.data.type;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
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
 * Marker interface for collections, maps and iterators that are unmodifiable.
 * <p/>
 * This interface enables testing such as:
 * <pre>
 * if (coll instanceof Unmodifiable) {
 *   coll = new ArrayList(coll);
 * }
 * // now we know coll is modifiable
 * </pre>
 * Of course all this only works if you use the Unmodifiable classes defined
 * in this library. If you use the JDK unmodifiable class via java util Collections
 * then the interface won't be there.
 *
 * @version $Id: Unmodifiable.java,v 1.2 2008/05/14 09:32:34 swd\stefan.damian Exp $
 */
public interface Unmodifiable
{
	// marker interface - no methods to implement
}

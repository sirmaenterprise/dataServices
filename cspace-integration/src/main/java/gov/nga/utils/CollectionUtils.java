/*
    Utils: CollectionUtils provides utilities for working with and manipulating collections
  
    Copyright (C) 2018 National Gallery of Art Washington DC
    Developers: David Beaudet

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package gov.nga.utils;

import java.lang.reflect.Array;
import java.util.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class CollectionUtils {
 
	/*public static <T1, T2> Map<T1, T2> newConcurrentMap() {
		return new ConcurrentHashMap<T1, T2>();
		//return new ConcurrentHashMap<T1, T2>();
		// Collections.synchronizedMap is not very scalable since all accessor and setter
		// methods block, so this is a better option for us performance-wise
	}*/
	
	// private static final Logger log = LoggerFactory.getLogger(CollectionUtils.class);

	public static <T1,T2> Map<T1,T2> newHashMap() {
		return new HashMap<T1,T2>();
	}
	
	public static <T1,T2> Map<T1,T2> newTreeMap(Comparator<T1> c) {
		return new TreeMap<T1, T2>(c);
	}

	public static <T1,T2> Map<T1,T2> newTreeMap() {
		return new TreeMap<T1, T2>();
	}
	
	public static <T> T[] arrayConcat(T[] a, T[] b) {
		T[] both = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, both, a.length, b.length);
		return both;
	}
	
	public static <T> T[] arrayConcat(T[] a, T b) {
		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(b.getClass(),1);
		c[0] = b;
		T[] both = Arrays.copyOf(a, a.length + 1);
		System.arraycopy(c, 0, both, a.length, 1);
		return both;
	}


/*	private static <T1> List<T1> newConcurrentList() {
		//return new ArrayList<T1>();
		return new CopyOnWriteArrayList<T1>();
		// Collections.ArrayList is not thread safe when iterating
		// but CopyOnWriteArrayList creates a new copy for every iterator and is recommended when 
		// a program has many reads but few writes - this cannot be sorted, so any sorting has
		// to take place prior to the copyonwritearraylist being created or if sorting is
		// needed afterwards, then client will have to call "toSortableList()" on it and then
		// apply a sort if they're doing it themselves.
	}
*/
	
	public static <T1> List<T1> newArrayList() {
		return new ArrayList<T1>();
	}

	@SafeVarargs
	public static <T1> List<T1> newArrayList(List<T1>... aList) {
		if (aList == null)
			return null;
		List<T1> all = CollectionUtils.newArrayList();
		for (List<T1> l : aList) {
			if ( l != null )
				all.addAll(l);
		}
		return all;
	}

	@SafeVarargs
	public static <T1> List<T1> newArrayList(T1[]... arrays) {
		if (arrays == null)
			return null;
		List<T1> all = CollectionUtils.newArrayList();
		for (T1[] l : arrays) {
			if ( l != null )
				all.addAll(Arrays.asList(l));
		}
		return all;
	}

	public static <T1> List<T1> newArrayList(List<T1> aList) {
		if (aList == null)
			return null;
		return new ArrayList<T1>(aList);
	}

	public static <T1> List<T1> newArrayList(Collection<T1> aColl) {
		if (aColl == null)
			return null;
		return new ArrayList<T1>(aColl);
	}
	
	public static <T1> List<T1> clearEmptyOrNull(List<T1> aList) {
		List<T1> nList = newArrayList();
		if (aList != null) {
			for (T1 t : aList) {
				if (t != null && !StringUtils.isNullOrEmpty(t.toString()))
					nList.add(t);
			}
		}
		return nList;
	}
	
	public static <T2, T1 extends T2> List<T2> newArrayListFromSubclass(Collection<T1> aList) {
		if (aList == null)
			return null;
		return new ArrayList<T2>(aList);
	}

	public static <T1> HashSet<T1> newHashSet() {
		return new HashSet<T1>();
	}

	public static <T1> HashSet<T1> newHashSet(HashSet<T1> aSet) {
		if (aSet == null)
			return null;
		return new HashSet<T1>(aSet);
	}

	public static <T1> HashSet<T1> newHashSet(Collection<T1> aColl) {
		if (aColl == null)
			return null;
		return new HashSet<T1>(aColl);
	}
	
	public static <T1> Set<T1> newTreeSet(Comparator<T1> c) {
		return new TreeSet<T1>(c);
	}

	public static <T1> Set<T1> newTreeSet() {
		return new TreeSet<T1>();
	}

	public static <T1> LinkedHashSet<T1> newLinkedHashSet() {
		return new LinkedHashSet<T1>();
	}

	public static <T1> LinkedHashSet<T1> newLinkedHashSet(HashSet<T1> aSet) {
		if (aSet == null)
			return null;
		return new LinkedHashSet<T1>(aSet);
	}

	public static <T1> LinkedHashSet<T1> newLinkedHashSet(Collection<T1> aColl) {
		if (aColl == null)
			return null;
		return new LinkedHashSet<T1>(aColl);
	}

	public static <T1> void sortAlphaDiacriticNormalized(List<T1> list) {
		Collections.sort(
			list,
			new Comparator<T1>() {
				public int compare(T1 a, T1 b) {
					return StringUtils.getDefaultCollator().compare(a, b);
				}
			}
		);
	}
	
	public static <T1> List<T1> toSortedAlphaDiacriticNormalizedList(Collection<T1> collection) {
		List<T1> list = newArrayList(collection);
		Collections.sort(
			list,
			new Comparator<T1>() {
				public int compare(T1 a, T1 b) {
					int j = StringUtils.getDefaultCollator().compare(a, b);
					return j;
				}
			}
		);
		return list;
	}

	public static <T1> List<T1> trimToSize(List<T1> aList, Integer maxSize) {
		if (aList == null || maxSize == null)
			return aList;
        if ( maxSize > 0 && aList.size() >= maxSize )
        	return aList.subList(0, maxSize);
        return aList;
	}

    public static  <K, T> Map<K, T> subtractByKeys(Map<K, T> m1, Set<K> m2) {
        Map<K, T> result = new HashMap<K, T>();
        for (Map.Entry<K, T> src: m1.entrySet()) {
            if (!m2.contains(src.getKey())) {
                result.put(src.getKey(), src.getValue());
            }
        }
        return result;
    }

	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        List<T> a = null;
        List<T> b = null;
        if (list1.size() > list2.size())
        {
        	a = list1;
        	b = list2;
        }
        else
        {
        	a = list2;
        	b = list1;
        }
        for (T t : a) {
            if(b.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }


/*	private static <T1> List<T1> toConcurrentList(List<T1> l) {
		//return l;
		if (l == null)
			return null;
		CopyOnWriteArrayList<T1> newList = new CopyOnWriteArrayList<T1>();
		newList.addAll(l);
		return newList;		
	}
	*/
}


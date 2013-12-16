/*
Copyright 2013 Universidad Politécnica de Madrid - Center for Open Middleware (http://www.centeropenmiddleware.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package es.upm.dit.xsdinferencer.datastructures;

/**
 * Common interface for {@linkplain SchemaElement} and {@linkplain SchemaAttribute} (i.e. anything 
 * that has a name, a namespace and a type, it does not matter if it is a simple or a complex type).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public interface SchemaNode {
	/**
	 * 
	 * @return The name of the node
	 */
	public String getName();
	/**
	 * 
	 * @return The namespace URI of the node
	 */
	public String getNamespace();
	/**
	 * Similar to equals but ignores the type
	 */
	public boolean equalsIgnoreType(SchemaNode otherNode);
}

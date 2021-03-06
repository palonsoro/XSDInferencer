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
/**
 * Main module which acts as input and output for the whole solution. 
 * If the solution is run as a standalone application, the {@link XSDInferencer#main(String[])} 
 * will act as entry point.
 * If the solution is used as a JAR library as a part of another Java application, this package contains 
 * all the classes which should be imported to start a complete inference process and get its results.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
package es.upm.dit.xsdinferencer;
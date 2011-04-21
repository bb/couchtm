/*
 * The Topic Maps API (TMAPI) was created collectively by
 * the membership of the tmapi-discuss mailing list
 * <http://lists.sourceforge.net/mailman/listinfo/tmapi-discuss>,
 * is hereby released into the public domain; and comes with 
 * NO WARRANTY.
 * 
 * No one owns TMAPI: you may use it freely in both commercial and
 * non-commercial applications, bundle it with your software
 * distribution, include it on a CD-ROM, list the source code in a
 * book, mirror the documentation at your own web site, or use it in
 * any other way you see fit.
 */
package de.topicmapslab.couchtm.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Provides a test suite which contains all test cases for TMAPI 2.0.
 * 
 * @author <a href="http://tmapi.org/">The TMAPI Project</a>
 * @author <a href="http://bock.be">Benjamin Bock</a>
 */
public class TestTMAPI extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        //suite.addTest(org.tmapi.AllTests.suite());
        return suite;
    }
}

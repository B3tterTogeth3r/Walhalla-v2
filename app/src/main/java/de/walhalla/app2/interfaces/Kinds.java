package de.walhalla.app2.interfaces;

import androidx.annotation.StringDef;

/**
 * The {@code Kinds} interface is to create <tt>@StringDef</tt> interfaces
 * to force programmers to use the correct values so there are less
 * compiling errors while up- and downloading data.
 *
 * @author B3tterTogeth3r
 * @version 1.1
 * @since 2.0
 */
public interface Kinds {
    String START = "start";
    String SIGN_IN = "sign_in";
    String SET_PASSWORD = "set_password";
    String CONTACT_INFORMATION = "set_contact_information";
    String FRATERNITY_DATA = "set_fraternity_data";
    String SET_IMAGE = "set_image";
    String CONTROL_DATA = "control_data";
    String IO = "io";
    String O = "o";
    String HO = "ho";

    /**
     * To restrict wrong inputs while trying to sign in or register
     * the programmer and user has to choose between one of the following:
     * <ul>
     *     <li>START</li>
     *     <li>SIGN_IN</li>
     *     <li>SET_PASSWORD</li>
     *     <li>CONTACT_INFORMATION</li>
     *     <li>FRATERNITY_DATA</li>
     *     <li>SET_IMAGE</li>
     *     <li>CONTROL_DATA</li>
     * </ul>
     */
    @StringDef({START, SIGN_IN, SET_PASSWORD, CONTACT_INFORMATION, FRATERNITY_DATA, SET_IMAGE, CONTROL_DATA})
    @interface NameOfState {
    }

    /**
     * To restrict wrong inputs for a collar, the programmer and user has to choose
     * between one of the following:
     * <ul><li>IO</li><li>O</li><li>HO</li></ul>
     */
    @StringDef({IO, O, HO})
    @interface Collar {
    }

}

package de.walhalla.app2.interfaces;

import androidx.annotation.StringDef;

/**
 * The {@code Kinds} interface is to create <tt>@StringDef</tt> interfaces
 * to force programmers to use the correct values so there are less
 * compiling errors while up- and downloading data.
 *
 * @author B3tterTogeth3r
 * @version 1.2
 * @since 2.0
 */
public interface Kinds {
    String START = "start";
    String SIGN_IN = "sign_in";
    String SET_PASSWORD = "set_password";
    /**
     * @deprecated should not be in use anymore
     */
    String CONTACT_INFORMATION = "set_contact_information";
    /**
     * @deprecated should not be in use anymore
     */
    String FRATERNITY_DATA = "set_fraternity_data";
    /**
     * @deprecated should not be in use anymore
     */
    String SET_IMAGE = "set_image";
    /**
     * @deprecated should not be in use anymore
     */
    String CONTROL_DATA = "control_data";
    String PROFILE_DATA = "profile_data";
    String COLLAR_IO = "io";
    String COLLAR_O = "o";
    String COLLAR_HO = "ho";
    String PUNCTUALITY_CT = "ct";
    String PUNCTUALITY_ST = "st";
    String TIME_WHOLE_DAY = "whole_day";
    String TIME_LATER = "later";
    String TIME_INFO = "info";
    String TIME_TITLE = "title";

    /**
     * To restrict wrong inputs while trying to sign in or register
     * the programmer and user has to choose between one of the following:
     * <ul>
     *     <li>START</li>
     *     <li>SIGN_IN</li>
     *     <li>SET_PASSWORD</li>
     *     <li>PROFILE_DATA</li>
     * </ul>
     */
    @StringDef({START, SIGN_IN, SET_PASSWORD, CONTACT_INFORMATION, FRATERNITY_DATA, SET_IMAGE, CONTROL_DATA, PROFILE_DATA})
    @interface NameOfState {
    }

    /**
     * To restrict wrong inputs for a collar, the programmer and user has to choose
     * between one of the following:
     * <ul><li>IO</li><li>O</li><li>HO</li></ul>
     */
    @StringDef({COLLAR_IO, COLLAR_O, COLLAR_HO})
    @interface Collar {
    }

    /**
     * To restrict wrong inputs and parsing errors only the following are allowed:
     * <ul><li>CT</li><li>ST</li><li>TIME_WHOLE_DAY</li><li>TIME_LATER</li>
     * <li>TIME_INFO</li><li>TIME_TITLE</li></ul>
     */
    @StringDef({PUNCTUALITY_CT, PUNCTUALITY_ST, TIME_WHOLE_DAY, TIME_LATER, TIME_INFO, TIME_TITLE})
    @interface Punctuality {
    }
}

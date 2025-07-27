package com.dspread.demoui.activity.nibssImpl.utils;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;


public class StringSplitter {

    /**
     * A method for splitting a string in J2ME.
     *
     *
     * java.lang.String#split(String, int).
     * @return An array of strings.
     */
    public static String amountFormat(double d)
    {
        String str = new DecimalFormat("#,###.##").format(d);
        return str;
    }
    public static String amountFormat(String d)
    {
        double dx = Double.parseDouble(d);
        String str = new DecimalFormat("#,###.00").format(dx);
        return str;
    }
    public static String[] split(String splitStr, String delimiter, int limit) {
// some input validation / short-circuiting
        if (delimiter == null || delimiter.length() == 0) {
            return new String[]{splitStr};
        } else if (splitStr == null) {
            return new String[0];
        }

// enabling switches based on the 'limit' parameter
        boolean arrayCanHaveAnyLength = false;
        int maximumSplits = Integer.MAX_VALUE;
        boolean dropTailingDelimiters = true;
        if (limit < 0) {
            arrayCanHaveAnyLength = true;
            maximumSplits = Integer.MAX_VALUE;
            dropTailingDelimiters = false;
        } else if (limit > 0) {
            arrayCanHaveAnyLength = false;
            maximumSplits = limit - 1;
            dropTailingDelimiters = false;
        }

        StringBuffer token = new StringBuffer();
        Vector tokens = new Vector();
        char[] chars = splitStr.toCharArray();
        boolean lastWasDelimiter = false;
        int splitCounter = 0;
        for (int i = 0; i < chars.length; i++) {
// check for a delimiter
            if (i + delimiter.length() <= chars.length && splitCounter < maximumSplits) {
                String candidate = new String(chars, i, delimiter.length());
                if (candidate.equals(delimiter)) {
                    tokens.addElement(token.toString());
                    token.setLength(0);

                    lastWasDelimiter = true;
                    splitCounter++;
                    i = i + delimiter.length() - 1;

                    continue; // continue the for-loop

                }
            }

// this character does not start a delimiter -> append to the token
            token.append(chars[i]);
            lastWasDelimiter = false;
        }

// don't forget the "tail"...
        if (token.length() > 0 || (lastWasDelimiter && !dropTailingDelimiters)) {
            tokens.addElement(token.toString());
        }

// convert the vector into an array
        String[] splitArray = new String[tokens.size()];
        for (int i = 0; i < splitArray.length; i++) {
            splitArray[i] = (String) tokens.elementAt(i);
        }

        return splitArray;
    }


    public static String GenerateRandomTextNo(int l) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        char[] stringChars = new char[l];

        int x = 0; //stringChars.length;
        for (int i = 0; i < l; i++) //3
        {
            Random random = new Random();
            x = random.nextInt(chars.length());
            stringChars[i] = chars.charAt(x);
        }
        return new String(stringChars); // stringChars.toString();
    }

    public static String GetRefNumber(String type, int len) {

        String finalString = "";
        int x = 0;
        char[] stringChars = new char[len];
        for (int i = 0; i < len; i++) //4
        {
            Random random = new Random();
            x = random.nextInt(9);

            stringChars[i] = Integer.toString(x).toCharArray()[0];
        }


        finalString = new String(stringChars);
        finalString = type + finalString;
        return finalString.trim();
    }



}




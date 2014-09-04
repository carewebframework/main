/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.springframework.context.MessageSource;

/**
 * Utility methods for managing strings.
 */
public class StrUtil {
    
    public static final String U = "^";
    
    public static final String CRLF = "\r\n";
    
    public static final String CRLF2 = CRLF + CRLF;
    
    public static final String LINE_TERMINATOR = "\n";
    
    public static final String CHARSET = CharEncoding.UTF_8;
    
    private static MessageSource messageSource;
    
    /**
     * Splits a string using the specified delimiter.
     * 
     * @param text The string to split.
     * @param delimiter The delimiter for the split operation.
     * @return A string array containing the split values. The returned array is guaranteed not to
     *         contain null values by replacing every occurrence of a null value with an empty
     *         string.
     */
    public static String[] split(String text, String delimiter) {
        return split(text, delimiter, 0);
    }
    
    /**
     * Splits a string using the specified delimiter.
     * 
     * @param text The string to split.
     * @param delimiter The delimiter for the split operation.
     * @param count Specifies a minimum number of elements to be returned. If the result of the
     *            split operation results in fewer elements, the returned array is expanded to
     *            contain the minimum number of elements.
     * @return A string array containing the split values. The returned array is guaranteed not to
     *         contain null values by replacing every occurrence of a null value with an empty
     *         string.
     */
    public static String[] split(String text, String delimiter, int count) {
        return split(text, delimiter, count, true);
    }
    
    /**
     * Splits a string using the specified delimiter.
     * 
     * @param text The string to split.
     * @param delimiter The delimiter for the split operation.
     * @param count Specifies a minimum number of elements to be returned. If the result of the
     *            split operation results in fewer elements, the returned array is expanded to
     *            contain the minimum number of elements.
     * @param nonull If true, the returned array is guaranteed not to contain null values by
     *            replacing every occurrence of a null value with an empty string.
     * @return A string array containing the split values.
     */
    public static String[] split(String text, String delimiter, int count, boolean nonull) {
        String[] pcs = text == null ? new String[count] : StringUtils
                .splitByWholeSeparatorPreserveAllTokens(text, delimiter);
        pcs = pcs.length >= count ? pcs : Arrays.copyOf(pcs, count);
        
        if (nonull) {
            for (int i = 0; i < pcs.length; i++) {
                if (pcs[i] == null) {
                    pcs[i] = "";
                }
            }
        }
        
        return pcs;
    }
    
    /**
     * Truncates a string if it exceeds a maximum length, appending an ellipsis to the end.
     * 
     * @param value String value to modify.
     * @param maxLength Maximum length for string.
     * @return Original string if less than or equal to the maximum length. Otherwise, a truncated
     *         string with an ellipsis appended.
     */
    public static String strTruncate(String value, int maxLength) {
        return value == null ? null : value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }
    
    /**
     * Append a value to a text string, using default separator (comma).
     * 
     * @param text Text string to append to.
     * @param value Value to append.
     * @return Text string with value appended.
     */
    public static String strAppend(String text, String value) {
        return strAppend(text, value, ", ");
    }
    
    /**
     * Append a value to a text string, using specified separator.
     * 
     * @param text Text string to append to.
     * @param value Value to append.
     * @param separator String to use as separator.
     * @return Text string with value appended.
     */
    public static String strAppend(String text, String value, String separator) {
        if (text == null) {
            text = "";
        }
        
        if (value == null || value.isEmpty()) {
            return text;
        }
        
        return text + (text.isEmpty() ? "" : separator) + value;
    }
    
    /**
     * Returns the first piece of text as delimited by delimiter.
     * 
     * @param text Text to piece.
     * @param delimiter Delimiter for piecing.
     * @return First piece of text.
     */
    public static String piece(String text, String delimiter) {
        return piece(text, delimiter, 1);
    }
    
    /**
     * Returns the specified piece of text as delimited by delimiter.
     * 
     * @param text Text to piece.
     * @param delimiter Delimiter for piecing.
     * @param position Position of piece to extract.
     * @return The requested text piece.
     */
    public static String piece(String text, String delimiter, int position) {
        return piece(text, delimiter, position, position);
    }
    
    /**
     * Performs the equivalent of the MUMPS piece function.
     * 
     * @param text Text to piece.
     * @param delimiter Delimiter for piecing.
     * @param start First piece to return.
     * @param end Last piece to return.
     * @return The requested text pieces.
     */
    public static String piece(String text, String delimiter, int start, int end) {
        String[] pcs = split(text, delimiter);
        StringBuilder result = new StringBuilder(text.length());
        
        if (start < 1) {
            start = 1;
        }
        
        if (start > pcs.length || start > end) {
            return "";
        }
        
        end = (end <= pcs.length ? end : pcs.length) - 1;
        
        for (int i = --start; i <= end; i++) {
            result.append(pcs[i]);
            
            if (i < end) {
                result.append(delimiter);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Extracts an integer portion at the beginning of a string. Parsing stops when a non-digit
     * character or the end of the string is encountered.
     * 
     * @param value String value to parse
     * @return The integer value at the beginning of the string.
     */
    public static int extractInt(String value) {
        return toInt(extractIntPrefix(value));
    }
    
    /**
     * Extracts an integer portion at the beginning of a string. Parsing stops when a non-digit
     * character or the end of the string is encountered.
     * 
     * @param value String value to parse
     * @return The integer prefix (as a string) of the parsed string.
     */
    public static String extractIntPrefix(String value) {
        int pos = 0;
        
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        
        if (value.startsWith("+") || value.startsWith("-")) {
            pos++;
        }
        
        while (pos < value.length()) {
            if (Character.isDigit(value.charAt(pos))) {
                pos++;
            } else {
                break;
            }
        }
        
        return value.substring(0, pos);
    }
    
    /**
     * Strip enclosing quotes (double or single) from a string.
     * 
     * @param value Input value.
     * @return Input value without the enclosing quotes.
     */
    public static String stripQuotes(String value) {
        String qt = value == null ? null : value.startsWith("\"") ? "\"" : value.startsWith("'") ? "'" : null;
        
        if (qt != null && value.endsWith(qt)) {
            value = value.substring(1, value.length() - 1);
        }
        
        return value;
    }
    
    /**
     * Converts a text value to a boolean result.
     * 
     * @param text Value to convert. Non-zero numeric values or values beginning with a T, t, Y, or
     *            y evaluate to true.
     * @return The result of the conversion.
     */
    public static boolean toBoolean(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        
        String char1 = text.substring(0, 1).toLowerCase();
        return "y".equals(char1) || "t".equals(char1) || NumberUtils.toInt(text) != 0;
    }
    
    /**
     * Converts a text value to an integer.
     * 
     * @param text Value to convert.
     * @return The result of the conversion.
     */
    public static int toInt(String text) {
        return NumberUtils.toInt(text);
    }
    
    /**
     * Converts a text value to a long integer.
     * 
     * @param text Value to convert.
     * @return The result of the conversion.
     */
    public static long toLong(String text) {
        return NumberUtils.toLong(text);
    }
    
    /**
     * Converts a text value to a double.
     * 
     * @param text Value to convert.
     * @return The result of the conversion.
     */
    public static double toDouble(String text) {
        return NumberUtils.toDouble(text);
    }
    
    /**
     * Converts a string containing a '\n'-delimited list of elements to a string list.
     * 
     * @param text The string containing the '\n'-delimited elements.
     * @return A list containing the parsed elements.
     */
    public static List<String> toList(String text) {
        return toList(text, null, LINE_TERMINATOR);
    }
    
    /**
     * Converts a string containing a delimited list of elements to a string list.
     * 
     * @param text The string containing the delimited elements.
     * @param delimiter The delimiter that separates the elements in the input string.
     * @return A list containing the parsed elements.
     */
    public static List<String> toList(String text, String delimiter) {
        return toList(text, null, delimiter);
    }
    
    /**
     * Converts a string containing a '\n'-delimited list of elements to a string list.
     * 
     * @param text The string containing the '\n'-delimited elements.
     * @param list The string list that will receive the parsed elements. If null, a new list is
     *            created. If not null, the list is cleared before adding elements.
     * @return A list containing the parsed elements. If the input list was not null, that instance
     *         is returned. Otherwise, a newly created list is returned.
     */
    public static List<String> toList(String text, List<String> list) {
        return toList(text, list, LINE_TERMINATOR);
    }
    
    /**
     * Converts a string containing a delimited list of elements to a string list.
     * 
     * @param text The string containing the delimited elements.
     * @param list The string list that will receive the parsed elements. If null, a new list is
     *            created. If not null, the list is cleared before adding elements.
     * @param delimiter The delimiter that separates the elements in the input string.
     * @return A list containing the parsed elements. If the input list was not null, that instance
     *         is returned. Otherwise, a newly created list is returned.
     */
    public static List<String> toList(String text, List<String> list, String delimiter) {
        if (list == null) {
            list = new ArrayList<String>();
        } else {
            list.clear();
        }
        
        if (text == null) {
            return list;
        }
        
        String[] pcs;
        int size;
        
        if (delimiter == null || delimiter.isEmpty()) {
            pcs = new String[] { text };
            size = 1;
        } else {
            pcs = split(text, delimiter);
            size = text.endsWith(delimiter) ? pcs.length - 1 : pcs.length;
        }
        
        for (int i = 0; i < size; i++) {
            list.add(pcs[i]);
        }
        
        return list;
    }
    
    /**
     * Builds a newline-delimited string from a list.
     * 
     * @param list The list of strings to concatenate.
     * @return A string contains the list of elements separated by the newline character.
     */
    public static String fromList(Iterable<String> list) {
        return fromList(list, "\n");
    }
    
    /**
     * Builds a delimited string from a list.
     * 
     * @param list The list of strings to concatenate.
     * @param delimiter The delimiter to use to separate elements.
     * @return A string contains the list of elements separated by the specified delimiter.
     */
    public static String fromList(Iterable<String> list, String delimiter) {
        return fromList(list, delimiter, "");
    }
    
    /**
     * Builds a delimited string from a list.
     * 
     * @param list The list of strings to concatenate.
     * @param delimiter The delimiter to use to separate elements.
     * @param dflt The default value to use if a list entry is null.
     * @return A string contains the list of elements separated by the specified delimiter.
     */
    public static String fromList(Iterable<String> list, String delimiter, String dflt) {
        StringBuilder sb = new StringBuilder();
        
        for (String ln : list) {
            ln = ln == null ? dflt : ln;
            
            if (ln != null) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                
                sb.append(ln);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Replaces one set of characters with another. Each character in the <b>from</b> parameter is
     * replaced by the character in the corresponding position in the <b>to</b> parameter. If no
     * corresponding character is present, the character is removed instead.
     * 
     * @param text The string to modify.
     * @param from The list of characters to be replaced.
     * @param to The list of replacement characters.
     * @return The modified input string.
     */
    public static String xlate(String text, String from, String to) {
        return StringUtils.replaceChars(text, from, to);
    }
    
    /**
     * Returns the message source for resolving messages.
     * 
     * @return messageSource
     */
    public static MessageSource getMessageSource() {
        return messageSource;
    }
    
    /**
     * Sets a message source for resolving messages.
     * 
     * @param messageSource
     */
    public static void setMessageSource(MessageSource messageSource) {
        StrUtil.messageSource = messageSource;
    }
    
    /**
     * Formats a message. If the message begins with "@", the remainder is assumed to be a label
     * name that is resolved.
     * 
     * @param msg Message to format or, if starts with "@", a label reference.
     * @param locale Optional locale
     * @param args Optional replaceable parameters.
     * @return The formatted message.
     */
    public static String formatMessage(String msg, Locale locale, Object... args) {
        if (msg == null || msg.isEmpty()) {
            return msg;
        }
        
        if (messageSource != null && msg.startsWith("@")) {
            return messageSource.getMessage(msg.substring(1), args, locale);
        }
        
        if (args != null && args.length > 0) {
            return String.format(locale, msg, args);
        }
        
        return msg;
    }
    
    /**
     * Formats a message. If the message begins with "@", the remainder is assumed to be a label
     * name that is resolved.
     * 
     * @param msg Message to format or, if starts with "@", a label reference.
     * @param args Optional replaceable parameters.
     * @return The formatted message.
     */
    public static String formatMessage(String msg, Object... args) {
        return formatMessage(msg, Locale.getDefault(), args);
    }
    
    /**
     * Enforce static class.
     */
    private StrUtil() {
    };
}

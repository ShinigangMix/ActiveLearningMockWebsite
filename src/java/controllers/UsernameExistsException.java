
package controllers;

import javax.servlet.ServletException;

public class UsernameExistsException extends ServletException{
    public UsernameExistsException(String message) {
   super(message);   
}}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author Joshua
 */

import javax.servlet.ServletException;

public class AuthenticationException extends ServletException {
    public AuthenticationException(String message) {
   super(message);    

    }
}
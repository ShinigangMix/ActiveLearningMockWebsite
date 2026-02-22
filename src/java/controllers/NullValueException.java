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

public class NullValueException extends ServletException {
    public NullValueException(String message) {
        super(message);
    }
}
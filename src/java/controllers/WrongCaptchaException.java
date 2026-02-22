/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import javax.servlet.ServletException;

/**
 *
 * @author Joshua
 */
public class WrongCaptchaException extends ServletException{
    public WrongCaptchaException(String message) {
   super(message);   
}}
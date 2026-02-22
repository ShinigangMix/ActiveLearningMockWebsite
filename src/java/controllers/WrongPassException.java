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
public class WrongPassException extends ServletException{
    public WrongPassException(String message) {
   super(message);   
}}
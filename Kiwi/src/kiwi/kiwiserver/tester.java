/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiwi.kiwiserver;

/**
 *
 * @author Tala Ross(rsstal002)
 */
public class tester {
    public static void main (String [] args) {
        String output = String.format("%-14s|", 1001) + String.format("%-50s|", "Murphy") + String.format("%-50s|", "Diane")+"\n";
        System.out.print(output);
        output = String.format("%-14s|", 1001) + String.format("%-50s|", "Murphjfvjhy") + String.format("%-50s|", "Dne");
        System.out.println(output);
    }
}

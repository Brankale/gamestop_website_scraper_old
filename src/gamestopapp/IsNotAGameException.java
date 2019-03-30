/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestopapp;

/**
 *
 * @author Utente
 */
public class IsNotAGameException extends RuntimeException {

    @Override
    public String toString() {
        return "This is not a Game!";
    }
    
}

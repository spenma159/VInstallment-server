// IVInstallmentInterface.aidl
package com.example.vinstallment;

// Declare any non-default types here with import statements

interface IVInstallmentInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void reminderInstallment();
    void oneDayAfter();
    void twoDayAfter();
    void threeDayAfter();
    void clearPunishment();
}
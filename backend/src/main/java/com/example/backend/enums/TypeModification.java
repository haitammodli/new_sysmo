package com.example.backend.enums;

public enum TypeModification {
    // --- ERREURS EN INTERNE ---
    ERREUR_TAXATION,             // Erreur de taxation sur système
    ERREUR_POIDS,                // Erreur de poids
    ERREUR_CALCUL_PARAMETRAGE,   // Erreur de calcul et de paramétrage

    // --- DEMANDES CLIENTS ---
    ERREUR_DESTINATION_CLIENT,   // Erreur de destination par le client
    MODIFICATION_DEMANDEE_CLIENT,// Modification demandée par le client
    MODIFICATION_VALEUR_DECLARE, // Modification Valeur déclarée

    // --- FINANCIER & SERVICES ---
    ANNULATION_ENCAISSEMENT,     // Annulation d'encaissement
    REMISE_MONTANT,              // Remise sur montant
    SERVICE_GRATUIT              // Service gratuit
}

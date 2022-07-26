package ca.uwaterloo.cs.db

import ca.uwaterloo.cs.bemodels.SignUpFarmer
import ca.uwaterloo.cs.bemodels.SignUpWorker
import ca.uwaterloo.cs.dbmodels.*
import ca.uwaterloo.cs.product.ProductInformation

class ContentIngestion {

    fun getCompleteUserProfile(signUpFarmer: SignUpFarmer): CompleteUserProfile{
        return CompleteUserProfile(
            signUpFarmer.firstName,
            signUpFarmer.familyName,
            "",
            null,
            "",
            signUpFarmer.enterpriseName,
            true,
            "",
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
    }

    fun getCompleteUserProfile(signUpWorker: SignUpWorker): CompleteUserProfile{
        return CompleteUserProfile(
            signUpWorker.firstName,
            signUpWorker.familyName,
            "",
            null,
            "",
            "",
            false,
            signUpWorker.farmerUserId,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
    }
}

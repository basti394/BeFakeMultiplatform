package ui.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pizza.xyz.befake.model.dtos.countrycode.Country
import pizza.xyz.befake.utils.Utils

class CountryCodeSelectionSheetViewModel(

) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries = _countries.asStateFlow()

    private val allCountries = emptyList<Country>()

    init {
        getCountries()
    }

    private fun getCountries() {
        _countries.value = emptyList()
    }

    fun searchCountry(value: String) {
        val prompt = value.lowercase()
        if (prompt.isEmpty()) {
            _countries.value = allCountries
            return
        }
        if (prompt.toDoubleOrNull() != null || prompt.startsWith("+")) {
            searchNumber(prompt)
        } else {
            searchName(prompt)
        }
    }

    private fun searchNumber(value: String) {
        _countries.value = allCountries.filter { it.dialCode.contains(value) }
    }

    private fun searchName(value: String) {
        _countries.value = allCountries.filter { it.name.lowercase().contains(value) }
    }
}
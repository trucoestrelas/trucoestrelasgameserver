class Baralho {

    private val cartas: MutableList<Carta> = mutableListOf()

    val valoresCartas: Map<String, Int> = mapOf(
        "4" to 1,
        "5" to 2,
        "6" to 3,
        "7" to 4,
        "Q" to 5,
        "J" to 6,
        "K" to 7,
        "A" to 8,
        "2" to 9,
        "3" to 10
    )

    fun embaralhar(){
        this.cartas.shuffle()
    }

    fun atualizaForcaManilhas(vira: Carta){
        var valorForcaVira = vira.forca + 1
        if (valorForcaVira > 10){
            valorForcaVira = 1
        }

        getCartasDeForca(valorForcaVira).forEach {carta ->
            when(carta.naipe){
                "Ouros"-> {carta.forca += 11}
                "Espadas" -> {carta.forca += 12}
                "Copas" -> {carta.forca += 13}
                "Paus" -> {carta.forca += 14}
            }
        }
    }

    fun distribuirCartas(numCartas: Int): List<Carta> {
        return cartas.take(numCartas).also { cartas.removeAll(it) }
    }

    fun getCartasDeForca(forca: Int): List<Carta> {
        return cartas.filter { it.forca == forca }
    }

    fun getMaiorCarta(cartasJogadas: MutableList<Carta>){

    }

    init {
        val naipes = listOf("Ouros", "Espadas", "Copas", "Paus") // Ouros, Espadas, Copas, Paus

        for ((chave, valor) in valoresCartas) {
            for (naipe in naipes) {
                cartas.add(Carta(chave, naipe, false, valor, null))
            }
        }
    }
}
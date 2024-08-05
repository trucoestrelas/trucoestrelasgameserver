data class Jogador (
    val id: String,
    val idSocket: String,
    val nome: String,
    val time: Time,
    val mao: MutableList<Carta> = mutableListOf()
){
    fun receberCartas(cartas: List<Carta>) {
        mao.addAll(cartas)
    }

    fun jogarCarta(indiceCarta: Int): Carta? {
        return if (mao.isNotEmpty()) {
            if (indiceCarta <= mao.size)
                mao.removeAt(indiceCarta)
            else
                mao.removeAt(0)
        }else{
            null
        }
    }


}
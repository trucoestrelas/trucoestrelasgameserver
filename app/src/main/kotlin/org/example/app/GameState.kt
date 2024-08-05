data class GameState (
    var quantJogadoresSala: Int,
    var listasJogadores: MutableList<Jogador>

){
    var rodadaAtual: Int = 1
    var valorRodadaAtual: Int = 1
    var turnoAtual: Int = 1
    var duracaoJogada: Int = 10
    var tentosTimeA: Int = 0
    var tentosTimeB: Int = 0
    var turnosTimeA: Int = 0
    var turnosTimeB: Int = 0
    var turnosVencidos = mutableListOf<Jogador>()
    var indicePe:Int = 0
    var status: String = ""
    var lastUpdate: Long = System.currentTimeMillis()
    var cartasJogadas = mutableListOf<Jogador>()
    var jogadorFugiu: Jogador? = null

}
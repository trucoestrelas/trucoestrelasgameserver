class Truco(
    private val onGameStateChanged: (gameState: GameState) -> Unit
) {

    companion object{
        const val DURACAO_JOGADA = 10
        const val DELAY_INICIO_JOGO = 5000L
        const val DELAY_ROUND = 2000L

        const val ACTION_TRUCO = 4
        const val ACTION_FUGIR = 5
    }


    private lateinit var gameState: GameState

    private var baralho = Baralho()

    private lateinit var vira : Carta
    private lateinit var timerJob: Job

    suspend fun jogar(quantJogadores: Int, jogadores: MutableList<Jogador>) = coroutineScope {
        gameState = GameState(quantJogadores, jogadores)
        sendMessageToAllUsers("INICIANDO_JOGO")
        delay(DELAY_INICIO_JOGO)
        while (!verificaFimJogo()){
            iniciaRodada()
            gameState.rodadaAtual += 1
        }

    }

    private fun sendMessageToAllUsers(msg: String){
        this.gameState.status = msg
        this.gameState.lastUpdate = System.currentTimeMillis()
        //enviar this.gameState
    }

    private fun sendMessageToJogador(msg: String, jogador: Jogador){
        this.gameState.status = msg
        this.gameState.lastUpdate = System.currentTimeMillis()
        //enviar this.gameState
    }

    //0-2 -> indice das carta a serem jogadas
    //4 --> Truco (6,9,12) , 5 --> Fugir
    private fun aguardarMoveJogador(msg: String, jogador: Jogador): Int{
        sendMessageToJogador(msg,jogador)
        startTimerJogada()

        //websocket.onActionReceived{ jogada ->
        //mock, sempre escolhe a primeira carta
        return 0
        //}
    }


    fun adicionarJogador(jogador: Jogador) {
        if (gameState.listasJogadores.size < gameState.quantJogadoresSala) {
            //gameState = GameState.JogadorAdicionado(jogador)
            onGameStateChanged.invoke(gameState)
            gameState.listasJogadores.add(jogador)
        }
    }

    suspend fun jogarCarta(jogador: Jogador, idxCarta: Int = 0): Carta? {
        timerJob.cancelAndJoin()
        return gameState.listasJogadores.filter { it.id == jogador.id }[0].jogarCarta(idxCarta)
    }

    suspend fun iniciaRodada(){
        sendMessageToAllUsers("INICIANDO_ROUND")
        delay(DELAY_ROUND)

        baralho = Baralho()
        baralho.embaralhar()
        sendMessageToAllUsers("EMBARALHANDO")
        delay(DELAY_ROUND)

        vira = baralho.distribuirCartas(1)[0]
        baralho.atualizaForcaManilhas(vira)
        sendMessageToAllUsers("DISTRIBUINDO_CARTAS")

        for (jogador in gameState.listasJogadores){
            jogador.receberCartas(baralho.distribuirCartas(3))
            sendMessageToJogador("SUAS_CARTAS", jogador)
        }

        this.gameState.turnoAtual = 1
        sendMessageToAllUsers("INICIANDO_TURNO")

        while (!verificaFimTurno()){
            iniciaTurno()
        }


    }

    private fun iniciaTurno(){
        val ordemJogada = if (this.gameState.turnoAtual == 1)
            gameState.listasJogadores.getOrdemJogadas(this.gameState.indicePe)
        else{
            gameState.listasJogadores.getOrdemJogadas(this.gameState.listasJogadores.indexOf(this.gameState.turnosVencidos.last()))
        }

        for (jogador in ordemJogada){
            val moveJogador = aguardarMoveJogador("AGUARDANDO_JOGADA_#${jogador.idSocket}", jogador)
        }
    }

    private fun verificaFimTurno(): Boolean {
        return (this.gameState.turnoAtual > 3 || this.gameState.turnosTimeA >= 2 || this.gameState.turnosTimeB >= 2 || this.gameState.jogadorFugiu != null)
    }


    private fun startTimerJogada(){
        runBlocking {
            timerJob = launch {
                countdownTimer(DURACAO_JOGADA)
            }
        }
    }

    fun getOrdemJogadaTurno(turno: Int): ArrayList<Jogador> {
        return gameState.listasJogadores.getOrdemJogadas(this.gameState.indicePe)
    }

    fun verificaFimJogo(): Boolean {
        return gameState.tentosTimeA >= 12 || gameState.tentosTimeB >= 12
    }

    suspend fun countdownTimer(seconds: Int) = coroutineScope {
        for (i in seconds downTo 0) {
            println("Tempo restante: $i segundos")
            delay(1000)
        }
        println("Tempo esgotado!")
    }



}
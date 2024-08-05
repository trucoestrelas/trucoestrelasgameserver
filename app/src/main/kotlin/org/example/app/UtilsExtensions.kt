fun MutableList<Jogador>.getOrdemJogadas(idxStart: Int): ArrayList<Jogador> {
    if (idxStart == 0)
        return ArrayList(this)

    val resultList = arrayListOf<Jogador>()
    resultList.addAll(this.subList(idxStart, this.size))
    resultList.addAll(this.subList(0, idxStart))
    return resultList
}
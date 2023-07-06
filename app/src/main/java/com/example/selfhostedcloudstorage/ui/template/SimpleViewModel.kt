import androidx.lifecycle.ViewModel

class SimpleViewModel : ViewModel() {
    var counter = 0

    fun incrementCounter() {
        counter++
    }
}

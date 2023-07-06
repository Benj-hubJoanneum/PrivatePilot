import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.selfhostedcloudstorage.databinding.TemplateActivityMainBinding

class TemplateMainActivity : AppCompatActivity() {

    private lateinit var binding: TemplateActivityMainBinding
    private val viewModel: SimpleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TemplateActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.counterTextView.text = "Counter: ${viewModel.counter}"

        binding.incrementButton.setOnClickListener {
            viewModel.incrementCounter()
            binding.counterTextView.text = "Counter: ${viewModel.counter}"
        }
    }
}

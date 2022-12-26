package ramana.example.httprestserver.example;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/greeting")
public class GreetingController {
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping
	public Greeting hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@GetMapping("/hello")
	public Greeting greeting(@RequestParam(value = "name") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@PostMapping("/echo")
	public Greeting echo(@RequestBody Greeting greeting) {
		return greeting;
	}

	@PostMapping("/testPost")
	public String testPost(@RequestBody Greeting greeting) {
		return "You invoked testPost";
	}

	@GetMapping("/testString")
	public String testString(@RequestParam(value = "name", defaultValue = "Ramana") String name) {
		return "You invoked testGet with name: " + name;
	}

	@GetMapping("/testInt")
	public int testInt() {
		return 10;
	}

	@GetMapping("/testBoolean")
	public boolean testBoolean() {
		return true;
	}

	@GetMapping("/testBigDecimal")
	public BigDecimal testBigDecimal() {
		return new BigDecimal(100);
	}
}

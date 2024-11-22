package net.chrisrichardson.ftgo.testutil.jsonschema;

import static org.junit.jupiter.api.Assertions.fail;

import com.github.erosb.jsonsKema.FormatValidationPolicy;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import com.github.erosb.jsonsKema.ValidatorConfig;

public class ValidatingJSONMapper {

	private Schema schema;

	public ValidatingJSONMapper(Schema schema) {
		this.schema = schema;
	}

	/**
	 * Create instance of this validator from schema file in classpath
	 * and return this object itself. Call validate against a json string.
	 * The method will fail if json string does not match schema definition.
	 * 
	 * @param schemaPath classpath:///path/to/your/schema.json
	 * @return this object itself.
	 */
	public static ValidatingJSONMapper forSchema(String schemaPath) {
		Schema schema = SchemaLoader.forURL(schemaPath).load();
		return new ValidatingJSONMapper(schema);
	}
	
	public void validate(String jsonObject) {
		Validator validator = Validator.create(schema, new ValidatorConfig(FormatValidationPolicy.ALWAYS));
		ValidationFailure failure =validator.validate(jsonObject);
		
		if(failure.getCauses().size() > 0) {
			fail("Schema validation failed: " + String.join(",", failure.getMessage()));
		}
	}
}

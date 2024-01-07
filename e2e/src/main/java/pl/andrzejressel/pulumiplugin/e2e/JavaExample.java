package pl.andrzejressel.pulumiplugin.e2e;

import com.pulumi.Context;
import com.pulumi.Pulumi;
import com.pulumi.command.local.Command;
import com.pulumi.command.local.CommandArgs;
import com.pulumi.command.local.LocalFunctions;
import com.pulumi.command.local.inputs.RunArgs;
import com.pulumi.command.local.outputs.RunResult;
import com.pulumi.random.RandomString;
import com.pulumi.random.RandomStringArgs;

public class JavaExample {
    public static void run(Context context) {
        var whoami = new Command("command", CommandArgs.builder().create("whoami").build());
        var randomString = new RandomString("random-string", RandomStringArgs.builder().length(8).build());
        context.export("string", randomString.result());
        context.export("whoami", whoami.stdout());
    }
}

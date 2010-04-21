

def run(command)
  puts command
  system command
end

task :jar do
  run "lein jar"
  run "mkdir tmp"
  run "cd tmp; jar -x < ../lib/smtp.jar"
  run "cd tmp; jar -x < ../lib/activation.jar"
  run "cd tmp; jar -x < ../lib/mailapi.jar"
  run "cd tmp; jar -x < ../mmemail.jar"
  run "cat _MANIFEST.MF >> tmp/META-INF/MANIFEST.MF"
  run "jar cfm mmemail.jar tmp/META-INF/MANIFEST.MF -C tmp/ ."
  run "rm -rf tmp"
end

task :push => [:jar] do                                               
  run "lein pom"
  run "scp mmemail.jar pom.xml clojars@clojars.org:"
end
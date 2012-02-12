var argv = require('optimist').argv;

if (argv.variable === 'k')
  console.log('k: %d', parseFloat(argv.x) / Math.tan(parseFloat(argv.theta)));
else if (argv.variable === 'theta')
  console.log('theta: %d', Math.atan(parseFloat(argv.x) / parseFloat(argv.k)));
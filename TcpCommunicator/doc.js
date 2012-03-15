var child_process = require('child_process')
  , fs = require('fs')
  , path = require('path');

child_process.exec('find ' + process.argv[process.argv.length - 1] + ' | grep ".html"', function (stderr, stdout) {
  if (stdout)
    console.log(stdout);
  if (stderr)
    console.warn(stderr);

  var next = function () {
    fs.mkdirSync('pdfs');

    var files = stdout.split('\n')
      , index = 0
      , threedigit = function (number) {
          var ret = number.toString();
          while (ret.length < 3)
            ret = '0' + ret;
          return ret;
        }
      , merge = function () {
          console.log(' ----- MERGING PDFS ----- ');
          child_process.exec('gs -dBATCH -dNOPAUSE -q -sDEVICE=pdfwrite -sOutputFile=pdfs/all-docs.pdf pdfs/*.pdf', function (stdout, stderr) {
            if (stdout)
              console.log(stdout);
            if (stderr)
              console.warn(stderr);
            
            child_process.exec('google-chrome pdfs/all-docs.pdf');
            console.log(' ----- DONE ----- ');
          });
        }
      , process = function () {
          var file = files[index];
          if (!path.existsSync(file)) {
            merge();
            return;
          }
          console.log('wkhtmltopdf "' + file + '" "./pdfs/' + threedigit(index) + '-' + file.replace(/\//g, '_').replace(/\./g, '_').replace(/\_html$/, '.pdf') + '"');
          child_process.exec('wkhtmltopdf "' + file + '" "./pdfs/' + threedigit(index) + '-' + file.replace(/\//g, '_').replace(/\./g, '_').replace(/_html$/, '.pdf') + '"', function (stderr, stdout) {
            if (stdout)
              console.log(stdout);
            if (stderr)
              console.warn(stderr);

            index++;
            if (index < files.length)
              process();
            else
              merge();
          });
        };

    process();
  };

  console.log(' ----- GENERATING PDFS ----- ');

  if (path.existsSync('pdfs'))
    child_process.exec('rm -f -r pdfs', function (stdout, stderr) {
      if (stdout)
        console.log(stdout);
      if (stderr)
        console.warn(stderr);

      next();
    });
  else
    next();
});

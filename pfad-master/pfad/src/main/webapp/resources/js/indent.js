//import Quill from 'quill'
Quill.debug(true)

let AlignStyle = Quill.import('attributors/style/align')
let BackgroundStyle = Quill.import('attributors/style/background')
let ColorStyle = Quill.import('attributors/style/color')
let DirectionStyle = Quill.import('attributors/style/direction')
let FontStyle = Quill.import('attributors/style/font')
let SizeStyle = Quill.import('attributors/style/size')    

Quill.register(AlignStyle, true);
Quill.register(BackgroundStyle, true);
Quill.register(ColorStyle, true);
Quill.register(DirectionStyle, true);
Quill.register(FontStyle, true);
Quill.register(SizeStyle, true);

const Parchment = Quill.import('parchment')

class IndentAttributor extends Parchment.Attributor.Style { // ClassAttributor {
  add (node, value) {
    if (value === 0) {
      this.remove(node)
      return true
    } else {
      return super.add(node, `${value}em`)
    }
  }
  value(node) {
  	var v = super.value(node);
    if (v.match('[0-9]+em$')) {
	  var ret = v.substr(-3,1);
	  return parseFloat(ret)
    }
  }
}

let IndentStyle = new IndentAttributor('indent', 'text-indent', {
  scope: Parchment.Scope.BLOCK,
  whitelist: ['1em', '2em', '3em', '4em', '5em', '6em', '7em', '8em', '9em']
})

Quill.register(IndentStyle, false)

class MarginAttributor extends Parchment.Attributor.Style { }

let MarginStyle = new MarginAttributor('margin', 'margin', {
  scope: Parchment.Scope.BLOCK,
  whitelist: ['0', '0.5', '1.0', '1.5', '2', '2.5', '3']
})

Quill.register(MarginStyle, false)


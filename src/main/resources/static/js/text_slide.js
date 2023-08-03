function _defineProperty(obj, key, value) {if (key in obj) {Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true });} else {obj[key] = value;}return obj;}const { Transition, TransitionGroup } = ReactTransitionGroup;


/**
 * -------------------------------------------------------
 * O Captain! My Captain!
 * -------------------------------------------------------
 *
 * https://en.wikipedia.org/wiki/O_Captain!_My_Captain!
 *
 */

const poem = setupCopy(`
O Captain! my Captain! our fearful trip is done;
The ship has weather'd every rack, the prize we sought is won;
The port is near, the bells I hear, the people all exulting,
While follow eyes the steady keel, the vessel grim and daring:

But O heart! heart! heart!
O the bleeding drops of red,
Where on the deck my Captain lies,
Fallen cold and dead.`);

const colors = [
'#f2bd4b',
'#789de8',
'#a27af9',
'#ef6ba7',
'#8064ef',
'#5be5be'];


function setupCopy(poem) {
  return poem.
  trim().
  split('\n') // split to lines
  .map(line => {
    const words = line.split(' ');
    return {
      size: words.length > 7 ? 8 : words.length > 4 ? 9 : 10,
      words };

  }) // split to words
  .filter(line => line.words[0] !== '') // filter any blank lines
  .concat([{ size: 3, words: ['replay'] }]); // replay
}

function cycle(value, total) {
  return (value % total + total) % total;
}

const Nav = ({ index, poem, navigate }) => /*#__PURE__*/
React.createElement("nav", null,
poem.map((line, i) => /*#__PURE__*/
React.createElement("button", {
  key: `${i}-nav-item`,
  className: classNames('nav-btn', { active: i === index }),
  onClick: e => {
    e.stopPropagation();
    navigate(i);
  } },

i + 1)));

const Line = ({ line, lineIdx, active }) => /*#__PURE__*/
React.createElement("div", {
  className: classNames('copy', {
    active }),

  style: { fontSize: line.size + 'vmin' } },

line.words.map((word, i) => /*#__PURE__*/
React.createElement(Slide, {
  in: active,
  key: `${lineIdx}-${i}word`,
  duration: 700 + i * 40 },

word)));


const Slide = ({ in: inProp, children, duration }) => {
  const defaultStyle = {
    opacity: 0 };


  const time = duration / 2;
  const delay = duration / 2;

  const transitionStyles = {
    entering: {
      animation: `slideUpIn ${time}ms cubic-bezier(0.165, 0.84, 0.44, 1) ${delay}ms forwards` },

    entered: {
      opacity: 1 },

    exiting: {
      animation: `slideUpOut ${time}ms cubic-bezier(0.895, 0.03, 0.685, 0.22) forwards` },

    exited: {
      opacity: 0 } };



  return /*#__PURE__*/(
    React.createElement(Transition, { in: inProp, timeout: duration, appear: true },
    (state) => /*#__PURE__*/
    React.createElement("span", { className: "outer-word" }, /*#__PURE__*/
    React.createElement("span", {
      className: "inner-word",
      style: {
        ...defaultStyle,
        ...transitionStyles[state] } },
    children))));

};

class Poem extends React.Component {
  constructor(props) {
    super(props);_defineProperty(this, "next",

    () => {
      clearInterval(this.interval);
      this.update();
    });_defineProperty(this, "update",

    () => {
      this.setState(({ currentIdx, colorIdx }) => ({
        currentIdx: cycle(currentIdx + 1, this.props.poem.length),
        colorIdx: cycle(colorIdx + 1, this.props.colors.length) }));

    });this.state = { currentIdx: 0, colorIdx: 0 };window.addEventListener('keydown', this.next);this.interval = setInterval(this.update, 3000);}

  render() {
    const { poem, colors } = this.props;
    const { currentIdx, colorIdx, complete, intro } = this.state;
    return /*#__PURE__*/(
      React.createElement("main", {
        onClick: this.next,
        style: {
          backgroundColor: colors[colorIdx] } }, /*#__PURE__*/


      React.createElement(Nav, {
        poem: poem,
        index: currentIdx,
        navigate: currentIdx => {
          clearInterval(this.interval);
          this.setState(() => ({ currentIdx }));
        } }),

      poem.map((line, i) => /*#__PURE__*/
      React.createElement(Line, {
        key: `${i}-line`,
        line: line,
        active: i === currentIdx,
        lineIdx: i }))));




  }}


const run = () => {
  const root = document.getElementById('root');

  ReactDOM.render( /*#__PURE__*/React.createElement(Poem, { poem: poem, colors: colors }), root);
};

run();

/*!
  Copyright (c) 2017 Jed Watson.
  Licensed under the MIT License (MIT), see
  http://jedwatson.github.io/classnames
*/

function classNames() {
  var classes = [];

  for (var i = 0; i < arguments.length; i++) {
    var arg = arguments[i];
    if (!arg) continue;

    var argType = typeof arg;

    if (argType === 'string' || argType === 'number') {
      classes.push(arg);
    } else if (Array.isArray(arg) && arg.length) {
      var inner = classNames.apply(null, arg);
      if (inner) {
        classes.push(inner);
      }
    } else if (argType === 'object') {
      for (var key in arg) {
        if (hasOwnProperty.call(arg, key) && arg[key]) {
          classes.push(key);
        }
      }
    }
  }

  return classes.join(' ');
}
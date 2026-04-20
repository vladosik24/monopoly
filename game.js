const boardData = [
  { name: "Старт", price: 0 },
  { name: "Київ", price: 100 },
  { name: "Львів", price: 120 },
  { name: "Податок", price: 0 },
  { name: "Одеса", price: 140 },
  { name: "Харків", price: 160 },
  { name: "Тюрма", price: 0 },
  { name: "Дніпро", price: 180 },
  { name: "Чернівці", price: 200 },
  { name: "Фініш", price: 0 }
];

let players = [
  { pos: 0, money: 1000, name: "Гравець 1" },
  { pos: 0, money: 1000, name: "Гравець 2" }
];

let currentPlayer = 0;
let ownership = {};

function renderBoard() {
  const board = document.getElementById("board");
  board.innerHTML = "";

  boardData.forEach((cell, index) => {
    const div = document.createElement("div");
    div.className = "cell";

    let text = cell.name;

    players.forEach((p, i) => {
      if (p.pos === index) {
        text += `<br><span class="player">P${i+1}</span>`;
      }
    });

    if (ownership[index] !== undefined) {
      text += `<br>🏠 P${ownership[index]+1}`;
    }

    div.innerHTML = text;
    board.appendChild(div);
  });
}

function rollDice() {
  let dice = Math.floor(Math.random() * 6) + 1;
  let player = players[currentPlayer];

  player.pos = (player.pos + dice) % boardData.length;

  document.getElementById("status").innerText =
    `${player.name} кинув ${dice}`;

  renderBoard();
}

function buyProperty() {
  let player = players[currentPlayer];
  let cell = boardData[player.pos];

  if (cell.price > 0 && ownership[player.pos] === undefined) {
    if (player.money >= cell.price) {
      player.money -= cell.price;
      ownership[player.pos] = currentPlayer;

      alert(`${player.name} купив ${cell.name}`);
    }
  }

  nextTurn();
}

function nextTurn() {
  currentPlayer = (currentPlayer + 1) % players.length;

  document.getElementById("status").innerText =
    `Хід: ${players[currentPlayer].name}`;

  renderBoard();
}

renderBoard();

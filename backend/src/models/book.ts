import mongoose from "mongoose";

const bookSchema = new mongoose.Schema({
  name: { type: String, required: true },
  author: String,
  isbn: { type: String, unique: true },
  created: { type: Date, default: Date.now },
});

const Book = mongoose.model("Book", bookSchema);

/*
EXAMPLE of usage

const book = new Book({
  name: "Introduction to Node.js",
  author: "Atta",
  isbn: "ABL-4567",
});

book
  .save()
  .then((book) => {
    console.log(book);
  })
  .catch((err) => {
    console.log(err);
  });
*/

<?php

namespace App\Http\Controllers;

use App\Category as CategoryModel;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    public function getAll(Request $request)
    {
        return response()->json(CategoryModel::all());
    }

    public function getById($id)
    {
        $category = CategoryModel::find($id);

        if ($category)
            return response()->json($category);

        return response()->json([], 404);
    }

    public function create(Request $request)
    {
        $category = CategoryModel::create($request->all());

        return response()->json($category, 201);
    }

    public function update($id, Request $request)
    {
        $category = CategoryModel::findOrFail($id);
        $category->update($request->all());

        return response()->json($category, 200);
    }

    public function delete($id)
    {
        CategoryModel::findOrFail($id)->delete();

        return response('Category was deleted successfuly', 200);
    }
}

<?php

namespace App\Http\Controllers;

use App\Memory as MemoryModel;
use Illuminate\Http\Request;

class MemoryController extends Controller
{
    public function getAll(Request $request)
    {
        $sortCriteria = $request['sortBy'] ? $request['sortBy'] : 'id';
        $fromId = $request['id'] ? $request['id'] : 1;

        switch ($sortCriteria) {
            case 'id':
                return isset($fromId) && is_numeric($fromId) ? response()->json(MemoryModel::select('*')->where('id', '>=', $fromId)->orderBy('id')->get()) : response()->json(MemoryModel::select('*')->orderBy('created_at', 'desc')->get());
                break;

            case 'date':
                return isset($fromId) && is_numeric($fromId) ? response()->json(MemoryModel::select('*')->where('id', '>=', $fromId)->orderBy('created_at', 'desc')->get()) : response()->json(MemoryModel::select('*')->orderBy('created_at', 'desc')->get());
                break;

            case 'title':
                return isset($fromId) && is_numeric($fromId) ? response()->json(MemoryModel::select('*')->where('id', '>=', $fromId)->orderBy('title')->get()) :  response()->json(MemoryModel::select('*')->orderBy('title')->get());
                break;

            default:
                return response()->json(MemoryModel::all());
                break;
        }
    }

    public function getById($id)
    {
        return response()->json(MemoryModel::find($id));
    }

    public function create(Request $request)
    {
        $memory = MemoryModel::create($request->all());

        return response()->json($memory, 201);
    }

    public function update($id, Request $request)
    {
        $memory = MemoryModel::findOrFail($id);
        $memory->update($request->all());

        return response()->json($memory, 200);
    }

    public function delete($id)
    {
        MemoryModel::findOrFail($id)->delete();

        return response('Memory was deleted successfuly', 200);
    }
}
